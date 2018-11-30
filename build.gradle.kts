import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.internal.HasConvention
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

plugins {
    base
    id("io.gitlab.arturbosch.detekt")
}

allprojects {
    group = "hu.bme.mit.theta.interchange.jani"
    version = "0.1-SNAPSHOT"

    apply(from = rootDir.resolve("repositories.gradle.kts"))
}

dependencies {
    detektPlugins(Libs.Detekt.formatting)
}

detekt {
    toolVersion = Versions.detekt
    config = files(rootProject.rootDir.resolve("detekt.yml"))
    filters = ".*/resources/.*,.*/build/.*"
}

gradle.projectsEvaluated {
    // Subproject configuration must be extracted after all subprojects have been evaluated.
    val subprojectSrcDirs = subprojects.flatMap { project ->
        val sourceSetContainer = project.extensions.findByType(SourceSetContainer::class)
        sourceSetContainer?.flatMap { sourceSet ->
            val convention = (sourceSet as? HasConvention)?.convention
            convention?.findPlugin(KotlinSourceSet::class)?.kotlin?.srcDirs ?: emptyList()
        } ?: emptyList()
    }.filter { it.exists() }
    val buildSrcDirs = listOf(rootDir.resolve("buildSrc/src/main/kotlin/"))
    val allSrcDirs = subprojectSrcDirs + buildSrcDirs

    detekt.input = files(allSrcDirs)
}
