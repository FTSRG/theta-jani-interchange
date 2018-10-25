dependencies {
    "testImplementation"(Libs.Junit.api)
    "testImplementation"(Libs.Junit.params)
    "testRuntimeOnly"(Libs.Junit.engine)
}

tasks {
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}
