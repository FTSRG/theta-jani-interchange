import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

apply<KotlinPlatformJvmPlugin>()
apply<JacocoPlugin>()
apply<MavenPublishPlugin>()

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

extensions.configure(PublishingExtension::class) {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

extensions.configure(JacocoPluginExtension::class) {
    toolVersion = Versions.jacoco
}
