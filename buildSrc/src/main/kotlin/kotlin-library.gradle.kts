import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

apply<KotlinPlatformJvmPlugin>()
apply<JacocoPlugin>()

dependencies {
    "implementation"(Libs.Kotlin.stdlib)
    "testImplementation"(Libs.Junit.api)
    "testImplementation"(Libs.Junit.params)
    "testRuntimeOnly"(Libs.Junit.engine)
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}

extensions.configure(JacocoPluginExtension::class) {
    toolVersion = Versions.jacoco
}
