plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(gradleKotlinDsl())

    compile(kotlin("gradle-plugin", "1.2.71"))
}

configurations.all {
    val isKotlinCompiler = name == "embeddedKotlin" || name.startsWith("kotlin") || name.startsWith("kapt")
    if (!isKotlinCompiler) {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin" && requested.module.name == "kotlin-compiler-embeddable") {
                useVersion("1.2.71")
            }
        }
    }
}

repositories {
    gradlePluginPortal()
    jcenter()
}
