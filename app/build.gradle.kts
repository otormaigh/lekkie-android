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
import ie.pennylabs.lekkie.plugin.toolbox.BuildConst
import ie.pennylabs.lekkie.plugin.toolbox.Deps
import ie.pennylabs.lekkie.plugin.toolbox.KeyStore
import ie.pennylabs.lekkie.plugin.toolbox.KeyStore.KEY_ALIAS
import ie.pennylabs.lekkie.plugin.toolbox.KeyStore.KEY_PASSWORD
import ie.pennylabs.lekkie.plugin.toolbox.KeyStore.STORE_PASSWORD

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
  compileSdkVersion(29)

  defaultConfig {
    applicationId = "ie.pennylabs.lekkie"
    minSdkVersion(21)
    targetSdkVersion(29)
    versionCode = BuildConst.Version.code
    versionName = BuildConst.Version.name
    the<BasePluginConvention>().archivesBaseName = "lekkie-$versionName"

    buildConfigField("String", "BASE_URL", project.properties["base_url"] as String)
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
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
      extra["enableCrashlytics"] = false
      signingConfig = signingConfigs.getByName("debug")
      applicationIdSuffix = ".debug"
      manifestPlaceholders = mapOf("google_maps_key" to project.properties["debug_map_key"])
    }

    named("release").configure {
      signingConfig = signingConfigs.getByName(if (file("../signing/release.keystore").exists()) "release" else "debug")
      manifestPlaceholders = mapOf("google_maps_key" to project.properties["release_map_key"])

      postprocessing.apply {
        proguardFiles("consumer-rules.pro")
        isRemoveUnusedResources = false
        isRemoveUnusedCode = true
        isOptimizeCode = true
        isObfuscate = true
      }
    }
  }

  packagingOptions {
    exclude("META-INF/main.kotlin_module")
    exclude("META-INF/atomicfu.kotlin_module")
  }
  lintOptions {
    setLintConfig(file("../quality/lint-config.xml"))
  }

  dependencies {
    implementation(project(":lib-data"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Deps.kotlin}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Deps.coroutines}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Deps.coroutines}")

    implementation("androidx.core:core-ktx:1.2.0-alpha01")
    implementation("com.google.android.material:material:1.1.0-alpha07")
    implementation("androidx.appcompat:appcompat:1.1.0-beta01")
    implementation("androidx.recyclerview:recyclerview:1.1.0-alpha06")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0-beta1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${Deps.lifecycle}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Deps.lifecycle}")
    implementation("androidx.lifecycle:lifecycle-extensions:${Deps.lifecycle}")
    implementation("com.google.android.gms:play-services-maps:16.1.0")
    implementation("androidx.work:work-runtime-ktx:2.1.0-alpha03")

    implementation("com.google.dagger:dagger:${Deps.dagger}")
    implementation("com.google.dagger:dagger-android:${Deps.dagger}")
    implementation("com.google.dagger:dagger-android-support:${Deps.dagger}")
    kapt("com.google.dagger:dagger-compiler:${Deps.dagger}")
    kapt("com.google.dagger:dagger-android-processor:${Deps.dagger}")

    implementation("com.squareup.retrofit2:retrofit:${Deps.retrofit2}")
    implementation("com.squareup.retrofit2:converter-moshi:${Deps.retrofit2}")
    implementation("com.squareup.okhttp3:okhttp:${Deps.okhttp3}")
    implementation("com.squareup.okhttp3:logging-interceptor:${Deps.okhttp3}")
    implementation("ru.gildor.coroutines:kotlin-coroutines-retrofit:1.1.0")
    debugImplementation("com.squareup.okhttp3:mockwebserver:${Deps.okhttp3}")

    implementation("com.jakewharton.timber:timber:4.7.1")
    implementation("com.jakewharton.threetenabp:threetenabp:1.2.1")

    implementation("com.google.firebase:firebase-analytics:16.5.0")
    implementation("com.google.firebase:firebase-core:16.0.9")
    implementation("com.google.firebase:firebase-perf:17.0.2")
    implementation("com.crashlytics.sdk.android:crashlytics:2.10.1")
  }
}

tasks.getByName("check").dependsOn(rootProject.tasks.getByName("detekt"))

androidExtensions {
  isExperimental = true
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