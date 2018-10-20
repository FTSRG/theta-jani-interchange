plugins {
    base
    kotlin("jvm") version "1.2.71" apply false
    id("io.gitlab.arturbosch.detekt") version "1.0.0.RC9.2" apply false
}

val jacksonVersion by extra { "2.9.7" }
val junitVersion by extra { "5.3.1" }
val detektVersion by extra { "1.0.0.RC9.2" }

allprojects {
    group = "hu.bme.mit.inf.theta.interchange.jani"
    version = "0.1-SNAPSHOT"

    repositories {
        jcenter()
    }
}
