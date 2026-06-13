dependencies {
    api(project(":reconcilem-core"))
    api(project(":reconcilem-csv"))

    implementation("org.springframework.boot:spring-boot-autoconfigure:3.5.10")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.5.10")

    testImplementation("org.springframework.boot:spring-boot-starter-test:3.5.10")
}