rootProject.name = "jani"

include("model", "interaction")

for (project in rootProject.children) {
    project.name = "${rootProject.name}-${project.name}"
}
