import java.io.FileOutputStream
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
    id("idea")
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

val kotlinVersion: String by project
val detektVersion: String by project

// https://github.com/gradle/kotlin-dsl/issues/430#issuecomment-414768887
fun gradlePlugin(id: String, version: String): String = "$id:$id.gradle.plugin:$version"

    dependencies {
    compileOnly(gradleKotlinDsl())
    compile(kotlin("gradle-plugin", kotlinVersion))
    compile(gradlePlugin("io.gitlab.arturbosch.detekt", detektVersion))
}

// Force the embeddable Kotlin compiler version to be the selected kotlinVersion.
// https://github.com/gradle/kotlin-dsl/issues/1207
configurations.all {
    val isKotlinCompiler = name == "embeddedKotlin" || name.startsWith("kotlin") || name.startsWith("kapt")
    if (!isKotlinCompiler) {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin" && requested.module.name == "kotlin-compiler-embeddable") {
                useVersion(kotlinVersion)
            }
        }
    }
}

val generatedKotlinSrcDir = buildDir.resolve("generated-src/kotlin")
val versionsClassName = "Versions"

sourceSets {
    named("main") {
        withConvention(KotlinSourceSet::class) {
            kotlin.srcDir(generatedKotlinSrcDir)
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
        description = "Update Versions.kt from buildSrc/gradle.properties"
        group = "build"

        val outputFile by extra { generatedKotlinSrcDir.resolve("$versionsClassName.kt") }
        val versionsSource by extra { generateVersionsSource() }

        doLast {
            generatedKotlinSrcDir.mkdirs()
            outputFile.writeText(versionsSource)
        }
    }

    named("compileKotlin", KotlinCompile::class) {
        dependsOn += generateVersions
    }
}

// Mark Versions.kt as generated. See https://stackoverflow.com/a/51089631 and
// https://discuss.gradle.org/t/how-do-i-get-intellij-to-recognize-gradle-generated-sources-dir/16847/5
idea {
    module {
        generatedSourceDirs.add(generatedKotlinSrcDir)
    }
}

repositories {
    gradlePluginPortal()
}

apply(from = rootDir.resolve("../repositories.gradle.kts"))
