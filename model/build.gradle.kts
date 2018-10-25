import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("kotlin-common")
    id("io.gitlab.arturbosch.detekt")
}

dependencies {
    api(Libs.`jackson-databind`)
    implementation(Libs.`jackson-module-kotlin`)
    detekt(Libs.`detekt-formatting`)
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
