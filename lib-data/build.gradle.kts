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


plugins {
  id("com.android.library")
  id("kotlin-android")
  id("kotlin-kapt")
}

android {
  compileSdkVersion(29)

  defaultConfig {
    minSdkVersion(21)
    targetSdkVersion(29)
    versionCode = BuildConst.Version.code
    versionName = BuildConst.Version.name
    consumerProguardFiles("consumer-rules.pro")

    javaCompileOptions {
      annotationProcessorOptions {
        arguments = mapOf("room.schemaLocation" to "$projectDir/schemas")
      }
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  buildTypes {
    named("debug")
    named("release")
  }
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Deps.kotlin}")
  implementation("androidx.room:room-ktx:${Deps.room}")
  implementation("androidx.room:room-runtime:${Deps.room}")
  kapt("androidx.room:room-compiler:${Deps.room}")
  implementation("androidx.lifecycle:lifecycle-livedata:${Deps.lifecycle}")

  implementation("com.squareup.moshi:moshi:1.9.0-SNAPSHOT")
  implementation("com.jakewharton.threetenabp:threetenabp:1.2.1")

  implementation("com.google.dagger:dagger:${Deps.dagger}")
  kapt("com.google.dagger:dagger-compiler:${Deps.dagger}")

  testImplementation("junit:junit:4.13-beta-3")
  testImplementation("com.squareup.assertj:assertj-android:1.2.0")
  testImplementation("org.threeten:threetenbp:1.4.0") {
    exclude(group = "com.jakewharton.threetenabp", module = "threetenabp")
  }
}
