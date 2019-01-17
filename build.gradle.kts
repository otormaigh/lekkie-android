buildscript {
  repositories {
    google()
    jcenter()

    maven("https://plugins.gradle.org/m2/")
    maven("https://maven.fabric.io/public")
    maven("http://storage.googleapis.com/r8-releases/raw/master")
  }
  dependencies {
    // see: https://github.com/otormaigh/lekkie-android/issues/31
    classpath("com.android.tools:r8:cf993049788d3d443b7b1007b396f96dd5e0598b")
    classpath("com.android.tools.build:gradle:3.5.0-alpha01")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${ie.pennylabs.lekkie.plugin.toolbox.Deps.kotlin}")
    classpath("com.github.triplet.gradle:play-publisher:2.0.0")
    classpath("com.google.gms:google-services:4.2.0")
    classpath("com.google.firebase:firebase-plugins:1.1.5")
    classpath("io.fabric.tools:gradle:1.27.0")
  }
}
allprojects {
  repositories {
    google()
    jcenter()
  }
}

apply<ie.pennylabs.lekkie.plugin.LekkiePlugin>()