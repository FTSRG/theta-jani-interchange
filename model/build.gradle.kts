import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("jacoco")
    id("io.gitlab.arturbosch.detekt")
}

val jacksonVersion: String by rootProject.extra
val junitVersion: String by rootProject.extra
val jacocoVersion: String by rootProject.extra
val detektVersion: String by rootProject.extra

dependencies {
    implementation(kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))
    api("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    detekt("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
}

tasks {
    withType<KotlinCompile> {
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

    jacoco {
        toolVersion = jacocoVersion
    }

    detekt {
        toolVersion = detektVersion
        config = files(rootProject.rootDir.resolve("detekt.yml"))
    }

    val processTestResources by getting(ProcessResources::class) {
        from(rootProject.rootDir.resolve("vendor/jani-models")) {
            include("**/*.jani")
        }
    }
}
