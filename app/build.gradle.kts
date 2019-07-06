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

    implementation("androidx.core:core-ktx:1.2.0-alpha02")
    implementation("com.google.android.material:material:1.1.0-alpha07")
    implementation("androidx.appcompat:appcompat:1.1.0-rc01")
    implementation("androidx.recyclerview:recyclerview:1.1.0-beta01")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0-alpha3") // https://issuetracker.google.com/issues/136103084
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${Deps.lifecycle}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Deps.lifecycle}")
    implementation("androidx.lifecycle:lifecycle-extensions:${Deps.lifecycle}")
    implementation("com.google.android.gms:play-services-maps:17.0.0")

    implementation("com.google.dagger:dagger:${Deps.dagger}")
    implementation("com.google.dagger:dagger-android:${Deps.dagger}")
    implementation("com.google.dagger:dagger-android-support:${Deps.dagger}")
    kapt("com.google.dagger:dagger-compiler:${Deps.dagger}")
    kapt("com.google.dagger:dagger-android-processor:${Deps.dagger}")

    implementation("com.jakewharton.timber:timber:4.7.1")
    implementation("com.jakewharton.threetenabp:threetenabp:1.2.1")

    implementation("com.google.firebase:firebase-analytics:17.0.0")
    implementation("com.google.firebase:firebase-core:17.0.0")
    implementation("com.google.firebase:firebase-perf:18.0.1")
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