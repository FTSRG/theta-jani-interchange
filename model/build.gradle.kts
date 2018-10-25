import hu.bme.mit.inf.theta.interchange.jani.buildsrc.Libraries
import hu.bme.mit.inf.theta.interchange.jani.buildsrc.Versions
import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("buildsrc.common.kotlin")
    id("io.gitlab.arturbosch.detekt")
}

dependencies {
    compile(Libraries.`jackson-databind`)
    implementation(Libraries.`jackson-module-kotlin`)

    detekt(Libraries.`detekt-formatting`)
}

tasks {
    detekt {
        toolVersion = Versions.detekt
        config = files(rootProject.rootDir.resolve("detekt.yml"))
    }

    named("processTestResources", ProcessResources::class) {
        from(rootProject.rootDir.resolve("vendor/jani-models")) {
            include("**/*.jani")
        }
    }
}
