/*
 * Copyright (C) 2018 Elliot Tormey
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
import lekkie.*
import lekkie.KeyStore.KEY_ALIAS
import lekkie.KeyStore.KEY_PASSWORD
import lekkie.KeyStore.STORE_PASSWORD
import lekkie.extension.runCommand
import org.jetbrains.kotlin.gradle.internal.AndroidExtensionsExtension

plugins {
  id("com.android.application")
  id("kotlin-android")
  id("kotlin-android-extensions")
  id("kotlin-kapt")
}

if (file("../enc.properties").exists()) {
  apply("../enc.properties")
}

android {
  compileSdkVersion(28)
  defaultConfig {
    applicationId = "ie.pennylabs.lekkie"
    minSdkVersion(21)
    targetSdkVersion(28)
    versionCode = BuildConst.Version.code
    versionName = BuildConst.Version.name
    the<BasePluginConvention>().archivesBaseName = "lekkie-$versionName"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    buildConfigField("String", "BASE_URL", project.properties["base_url"] as String)

    javaCompileOptions {
      annotationProcessorOptions {
        arguments = mapOf("room.schemaLocation" to "$projectDir/schemas")
      }
    }
  }

  signingConfigs {
    getByName("debug") {
      storeFile = file(KeyStore.DEBUG_STORE)
    }
    if (file(KeyStore.RELEASE_STORE).exists()) {
      register("release") {
        storeFile = file(KeyStore.RELEASE_STORE)
        storePassword = STORE_PASSWORD
        keyAlias = KEY_ALIAS
        keyPassword = KEY_PASSWORD
        isV2SigningEnabled = true
      }
    }
  }

  buildTypes {
    named("debug").configure {
      signingConfig = signingConfigs.getByName("debug")
      applicationIdSuffix = ".debug"
      manifestPlaceholders = mapOf("google_maps_key" to project.properties["debug_map_key"])
    }

    named("release").configure {
      signingConfig = signingConfigs.getByName(if (file("../signing/release.keystore").exists()) "release" else "debug")
      manifestPlaceholders = mapOf("google_maps_key" to project.properties["release_map_key"])

      postprocessing.apply {
        proguardFiles("proguard-rules.pro")
        isRemoveUnusedResources = false
        isRemoveUnusedCode = true
        isOptimizeCode = true
        isObfuscate = true
      }
    }
  }

  packagingOptions {
    exclude("META-INF/main.kotlin_modul")
  }
  lintOptions {
    setLintConfig(file("../quality/lint-config.xml"))
  }

  dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Deps.kotlin}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Deps.coroutines}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Deps.coroutines}")

    implementation("androidx.core:core-ktx:1.0.1")
    implementation("com.google.android.material:material:1.1.0-alpha02")
    implementation("androidx.appcompat:appcompat:1.0.2")
    implementation("androidx.recyclerview:recyclerview:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0-alpha3")
    implementation("androidx.lifecycle:lifecycle-livedata:2.0.0")
    implementation("com.google.android.gms:play-services-maps:16.0.0")
    implementation("androidx.room:room-runtime:${Deps.arch_room}")
    kapt("androidx.room:room-compiler:${Deps.arch_room}")

    implementation("com.google.dagger:dagger:${Deps.dagger}")
    implementation("com.google.dagger:dagger-android:${Deps.dagger}")
    implementation("com.google.dagger:dagger-android-support:${Deps.dagger}")
    kapt("com.google.dagger:dagger-compiler:${Deps.dagger}")
    kapt("com.google.dagger:dagger-android-processor:${Deps.dagger}")

    implementation("com.squareup.retrofit2:retrofit:${Deps.retrofit2}")
    implementation("com.squareup.retrofit2:converter-moshi:${Deps.retrofit2}")
    implementation("com.squareup.okhttp3:okhttp:${Deps.okhttp3}")
    implementation("com.squareup.okhttp3:logging-interceptor:${Deps.okhttp3}")
    implementation("ru.gildor.coroutines:kotlin-coroutines-retrofit:0.13.0-eap13")
    debugImplementation("com.squareup.okhttp3:mockwebserver:${Deps.okhttp3}")

    implementation("com.jakewharton.timber:timber:4.7.1")
    implementation("com.jakewharton.threetenabp:threetenabp:1.1.1")

    implementation("com.google.firebase:firebase-analytics:16.0.6")
    implementation("com.google.firebase:firebase-core:16.0.6")
    implementation("com.google.firebase:firebase-perf:16.2.3")
    implementation("com.crashlytics.sdk.android:crashlytics:2.9.8")

    testImplementation("junit:junit:4.12")
    testImplementation("com.squareup.assertj:assertj-android:1.2.0")
    testImplementation("org.threeten:threetenbp:1.3.6") {
      exclude(group = "com.jakewharton.threetenabp", module = "threetenabp")
    }
    androidTestImplementation("androidx.test:runner:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.1.1")
  }
}

tasks.getByName("check").dependsOn(rootProject.tasks.getByName("detekt"))

kapt {
  correctErrorTypes = true
  useBuildCache = true
  mapDiagnosticLocations = true
  arguments {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("moshi.generated", "javax.annotation.Generated")
    arg("dagger.formatGeneratedSource", "disabled")
  }
}

androidExtensions {
  configure(delegateClosureOf<AndroidExtensionsExtension> {
    isExperimental = true
  })
}

if (file(KeyStore.PLAY_JSON).exists()) {
  apply(plugin = "com.github.triplet.play")

  play {
    track = "internal"
    serviceAccountCredentials = file(KeyStore.PLAY_JSON)
    defaultToAppBundles = true
  }
}

if (file("google-services.json").exists()) {
  apply(plugin = "io.fabric")
  apply(plugin = "com.google.firebase.firebase-perf")
  apply(plugin = "com.google.gms.google-services")
}

tasks.register("prepareNextRelease") {
  group = "Releasing"
  description = "Prepares the project for the next release version, see: RELEASING.md"

  doLast {
    val newVersion = BuildConst.Version.name
      .replaceAfter("-", "")
      .removeSuffix("-")

    // 7. Create a new release branch `git checkout -b release-{versionName}`
    """git checkout -b release-$newVersion""".runCommand()

    // 8. Update previous `CHANGELOG.md` entry to append title with the build commit of that release
    """sed -i '''' -e '1 s/$/ - ${BuildConst.Git.shortHash}/' ${file("../CHANGELOG.md")}""".runCommand()

    // 9. Commit changes `git commit -am 'bump version to {versionName}'
    """git commit -am 'bump version to $newVersion'""".runCommand()
  }
}
tasks.register("generateChangelog") {
  group = "Releasing"
  description = "Generate a changelog based on the range of commits that triggered a build on CircleCI."

  doLast {
    var commitRange = System.getenv("CIRCLE_COMPARE_URL").split("/").last()
    if (commitRange.isEmpty()) System.getenv("CIRCLE_SHA1")
    if (!commitRange.contains("...")) commitRange = "HEAD^..$commitRange"
    logger.info("commitRange -> $commitRange")

    File("app/src/main/play/release-notes/en-GB/internal.txt").apply {
      createNewFile()
      writeText("""git log --pretty=-%s $commitRange""".runCommand() ?: "")
    }
  }
}

fun Project.play(configure: Action<com.github.triplet.gradle.play.PlayPublisherExtension>) {
  project.extensions.configure("play", configure)
}