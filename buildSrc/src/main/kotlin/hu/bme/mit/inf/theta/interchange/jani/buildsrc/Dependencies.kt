package hu.bme.mit.inf.theta.interchange.jani.buildsrc

object Versions {
    const val kotlin = "1.2.71"
    const val jackson = "2.9.7"
    const val junit = "5.3.1"
    const val jacoco = "0.8.2"
    const val detekt = "1.0.0.RC9.2"
}

object Libraries {
    val `kotlin-stdlib` = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
    val `jackson-databind` = "com.fasterxml.jackson.core:jackson-databind:${Versions.jackson}"
    val `jackson-module-kotlin` = "com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.jackson}"
    val `junit-jupiter-api` = "org.junit.jupiter:junit-jupiter-api:${Versions.junit}"
    val `junit-jupiter-params` = "org.junit.jupiter:junit-jupiter-params:${Versions.junit}"
    val `junit-jupiter-engine` = "org.junit.jupiter:junit-jupiter-engine:${Versions.junit}"
    val `detekt-formatting` = "io.gitlab.arturbosch.detekt:detekt-formatting:${Versions.detekt}"
}
