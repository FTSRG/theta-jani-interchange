import java.io.FileOutputStream
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

val kotlinVersion: String by project
val detektVersion: String by project

dependencies {
    compileOnly(gradleKotlinDsl())
    compile(kotlin("gradle-plugin", kotlinVersion))
    compile("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:$detektVersion")
}

val versionsClassName = "Versions"
val generatedVersionsKotlinSrcDir = buildDir.resolve("generated-sources/versions/kotlin")
val generatedVersionsFile = generatedVersionsKotlinSrcDir.resolve("$versionsClassName.kt")

sourceSets {
    named("main") {
        withConvention(KotlinSourceSet::class) {
            kotlin.srcDir(generatedVersionsKotlinSrcDir)
        }
    }
}

fun generateVersionsSource(): String {
    val text = StringBuilder()

    text.appendln("object $versionsClassName {")

    for (property in project.properties) {
        if (property.key.endsWith("Version")) {
            val keyWithoutVersion = property.key.substringBefore("Version")
            text.appendln("   const val `$keyWithoutVersion` = \"${property.value}\"")
        }
    }

    text.appendln("}")

    return text.toString()
}

tasks {
    val generateVersions by creating {
        description = "Updates Versions.kt from project properties."
        group = "build"
        outputs.dirs(generatedVersionsKotlinSrcDir)

        doLast {
            val versionsSource = generateVersionsSource()
            generatedVersionsKotlinSrcDir.mkdirs()
            generatedVersionsFile.writeText(versionsSource)
        }
    }

    named("compileKotlin", KotlinCompile::class) {
        dependsOn += generateVersions
    }
}

repositories {
    gradlePluginPortal()
}

apply(from = rootDir.resolve("../repositories.gradle.kts"))
