import lekkie.Deps

buildscript {
  repositories {
    google()
    jcenter()

    maven("https://maven.fabric.io/public")
    maven("http://storage.googleapis.com/r8-releases/raw/master")
  }
  dependencies {
    // see: https://github.com/otormaigh/lekkie-android/issues/31
    classpath("com.android.tools:r8:cf993049788d3d443b7b1007b396f96dd5e0598b")
    classpath("com.android.tools.build:gradle:3.4.0-alpha10")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.11")
    classpath("com.github.triplet.gradle:play-publisher:2.0.0")
    classpath("com.google.gms:google-services:4.2.0")
    classpath("com.google.firebase:firebase-plugins:1.1.5")
    classpath("io.fabric.tools:gradle:1.27.0")
  }
}

plugins {
  id("io.gitlab.arturbosch.detekt") version "1.0.0-RC12"
}

allprojects {
  repositories {
    google()
    jcenter()

    maven("https://jitpack.io")
  }
}

task<Delete>("clean") {
  delete = setOf(rootProject.buildDir)
}


tasks.withType(Wrapper::class.java) {
  gradleVersion = "5.1"
  distributionType = Wrapper.DistributionType.ALL
}

tasks.withType(io.gitlab.arturbosch.detekt.Detekt::class.java) {
  //  toolVersion = "1.0.0-RC12"
  config = files("$rootDir/detekt.yml")
  input = files("$rootDir/app")
  filters = project.objects.property(String::class.java).apply { set(".*Test.*,.*AndroidTest.*,.*/resources/.*,.*/tmp/.*") }
  reports {
    html {
      enabled = true
      destination = file("$projectDir/app/build/reports/detekt/detekt.html")
    }
  }
}