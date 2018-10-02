rootProject.name = "jani"

include("model")

rootProject.children.forEach {
    it.name = "${rootProject.name}-${it.name}"
}