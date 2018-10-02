import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    base
    kotlin("jvm") version "1.2.71" apply false
}

val jacksonVersion by extra { "2.9.7" }
val junitVersion by extra { "5.3.1" }

allprojects {
    group = "hu.bme.mit.inf.jani"
    version = "0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}

dependencies {
    subprojects.forEach {
        archives(it)
    }
}