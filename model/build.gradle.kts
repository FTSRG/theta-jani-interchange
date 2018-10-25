import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("kotlin-library")
}

dependencies {
    api(Libs.Jackson.databind)
    implementation(Libs.Jackson.kotlin)
}

tasks {
    named("processTestResources", ProcessResources::class) {
        from(rootProject.rootDir.resolve("vendor/jani-models")) {
            include("**/*.jani")
        }
    }
}
