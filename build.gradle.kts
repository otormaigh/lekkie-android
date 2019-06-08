import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  repositories {
    google()
    jcenter()

    maven("https://plugins.gradle.org/m2/")
    maven("https://maven.fabric.io/public")
  }
  dependencies {
    classpath("com.android.tools.build:gradle:3.6.0-alpha03")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${ie.pennylabs.lekkie.plugin.toolbox.Deps.kotlin}")
    classpath("com.github.triplet.gradle:play-publisher:2.2.1")
    classpath("com.google.gms:google-services:4.2.0")
    classpath("com.google.firebase:perf-plugin:1.2.1")
    classpath("io.fabric.tools:gradle:1.29.0")
  }
}
allprojects {
  repositories {
    google()
    jcenter()
    
    maven("https://jitpack.io")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
  }

  if (file("../enc.properties").exists()) {
    apply(from = "../enc.properties")
  }

  afterEvaluate {
    (project.extensions.findByName("kapt") as? KaptExtension)?.apply {
      arguments {
        arg("dagger.gradle.incremental", "enabled")
      }
      useBuildCache = true
    }
  }

  tasks.withType<KotlinCompile> {
    kotlinOptions {
      jvmTarget = "1.8"
      freeCompilerArgs = listOf("-Xallow-result-return-type")
    }
  }
}

apply<ie.pennylabs.lekkie.plugin.LekkiePlugin>()

task<Delete>("clean") {
  delete = setOf(rootProject.buildDir)
}

tasks.withType(Wrapper::class.java) {
  gradleVersion = "5.4.1"
  distributionType = Wrapper.DistributionType.ALL
}