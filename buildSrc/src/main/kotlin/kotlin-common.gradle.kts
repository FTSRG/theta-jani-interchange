import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

apply<KotlinPlatformJvmPlugin>()

dependencies {
    "implementation"(Libs.Kotlin.stdlib)
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}
