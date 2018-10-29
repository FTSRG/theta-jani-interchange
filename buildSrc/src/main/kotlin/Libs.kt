object Libs {
    object Kotlin {
        val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
    }

    object Kotlinx {
        object Serialization {
            val runtime = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Versions.serialization}"
        }
    }

    object Jackson {
        val databind = "com.fasterxml.jackson.core:jackson-databind:${Versions.jackson}"
        val kotlin = "com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.jackson}"
    }

    object Junit {
        val api = "org.junit.jupiter:junit-jupiter-api:${Versions.junit}"
        val params = "org.junit.jupiter:junit-jupiter-params:${Versions.junit}"
        val engine = "org.junit.jupiter:junit-jupiter-engine:${Versions.junit}"
    }

    object Detekt {
        val formatting = "io.gitlab.arturbosch.detekt:detekt-formatting:${Versions.detekt}"
    }
}
