import hu.bme.mit.inf.theta.interchange.jani.buildsrc.Versions

rootProject.name = "jani"

include("model")

rootProject.children.forEach {
    it.name = "${rootProject.name}-${it.name}"
}
