import hu.bme.mit.inf.theta.interchange.jani.buildsrc.Versions
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import java.net.URLClassLoader

plugins {
    base
    id("io.gitlab.arturbosch.detekt") version "1.0.0.RC9.2" apply false
}

allprojects {
    group = "hu.bme.mit.inf.theta.interchange.jani"
    version = "0.1-SNAPSHOT"

    repositories {
        jcenter()
    }
}
