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
import ie.pennylabs.lekkie.plugin.toolbox.*
import ie.pennylabs.lekkie.plugin.toolbox.KeyStore.KEY_ALIAS
import ie.pennylabs.lekkie.plugin.toolbox.KeyStore.KEY_PASSWORD
import ie.pennylabs.lekkie.plugin.toolbox.KeyStore.STORE_PASSWORD
import org.jetbrains.kotlin.gradle.internal.AndroidExtensionsExtension

plugins {
  id("com.android.application")
  id("kotlin-android")
  id("kotlin-android-extensions")
  id("kotlin-kapt")
}

if (file("../enc.properties").exists()) {
  apply(from = "../enc.properties")
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
    exclude("META-INF/main.kotlin_module")
  }
  lintOptions {
    setLintConfig(file("../quality/lint-config.xml"))
  }

  dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Deps.kotlin}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Deps.coroutines}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Deps.coroutines}")

    implementation("androidx.core:core-ktx:1.1.0-alpha03")
    implementation("com.google.android.material:material:1.1.0-alpha02")
    implementation("androidx.appcompat:appcompat:1.1.0-alpha01")
    implementation("androidx.recyclerview:recyclerview:1.1.0-alpha01")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0-alpha3")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${Deps.lifecycle}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Deps.lifecycle}")
    implementation("androidx.lifecycle:lifecycle-extensions:${Deps.lifecycle}")
    implementation("com.google.android.gms:play-services-maps:16.0.0")
    implementation("androidx.room:room-coroutines:${Deps.arch_room}")
    implementation("androidx.room:room-runtime:${Deps.arch_room}")
    kapt("androidx.room:room-compiler:${Deps.arch_room}")
    implementation("android.arch.work:work-runtime-ktx:1.0.0-beta02")

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
  useBuildCache = true
  arguments {
    arg("room.schemaLocation", "$projectDir/schemas")
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

fun Project.play(configure: Action<com.github.triplet.gradle.play.PlayPublisherExtension>) {
  project.extensions.configure("play", configure)
}