package hu.bme.mit.inf.theta.interchange.jani.buildsrc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class KotlinConventions : Plugin<Project> {
    override fun apply(target: Project): Unit = target.run {
        apply<KotlinPlatformJvmPlugin>()
        apply<JacocoPlugin>()

        dependencies {
            "implementation"(Libraries.`kotlin-stdlib`)
            "testImplementation"(Libraries.`junit-jupiter-api`)
            "testImplementation"(Libraries.`junit-jupiter-params`)
            "testRuntimeOnly"(Libraries.`junit-jupiter-engine`)
        }

        tasks.withType<KotlinCompile> {
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

        extensions.configure(JacocoPluginExtension::class) {
            toolVersion = Versions.jacoco
        }
    }
}