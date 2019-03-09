buildscript {
  repositories {
    google()
    jcenter()

    maven("https://plugins.gradle.org/m2/")
    maven("https://maven.fabric.io/public")
  }
  dependencies {
    classpath("com.android.tools.build:gradle:3.5.0-alpha06")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${ie.pennylabs.lekkie.plugin.toolbox.Deps.kotlin}")
    classpath("com.github.triplet.gradle:play-publisher:2.1.1")
    classpath("com.google.gms:google-services:4.2.0")
    classpath("com.google.firebase:firebase-plugins:1.1.5")
    classpath("io.fabric.tools:gradle:1.27.1")
  }
}
allprojects {
  repositories {
    google()
    jcenter()
  }
}

apply<ie.pennylabs.lekkie.plugin.LekkiePlugin>()