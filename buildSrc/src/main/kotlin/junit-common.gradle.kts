dependencies {
    "testImplementation"(Libs.`junit-jupiter-api`)
    "testImplementation"(Libs.`junit-jupiter-params`)
    "testRuntimeOnly"(Libs.`junit-jupiter-engine`)
}

tasks {
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}
