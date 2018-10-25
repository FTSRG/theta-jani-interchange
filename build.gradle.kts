plugins {
    base
}

allprojects {
    group = "hu.bme.mit.inf.theta.interchange.jani"
    version = "0.1-SNAPSHOT"

    apply(from = rootDir.resolve("repositories.gradle.kts"))
}
