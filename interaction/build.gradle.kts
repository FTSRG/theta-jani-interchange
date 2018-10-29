import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("kotlin-library")
}

dependencies {
    api(project(":jani-model"))
    api(Libs.Jackson.databind)
}
