import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("kotlin-library")
}

dependencies {
    api(Libs.`jackson-databind`)
    implementation(Libs.`jackson-module-kotlin`)
}

tasks {
    named("processTestResources", ProcessResources::class) {
        from(rootProject.rootDir.resolve("vendor/jani-models")) {
            include("**/*.jani")
        }
    }
}
