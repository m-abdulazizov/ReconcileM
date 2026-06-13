dependencies {
    api(project(":reconcilem-core"))
    api(project(":reconcilem-csv"))
    api(project(":reconcilem-jdbc"))

    implementation("org.springframework.boot:spring-boot-autoconfigure:3.5.10")
    implementation("org.springframework:spring-jdbc:6.2.2")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.5.10")

    testImplementation("org.springframework.boot:spring-boot-starter-test:3.5.10")
    testImplementation("org.springframework:spring-jdbc:6.2.2")
    testRuntimeOnly("com.h2database:h2:2.2.224")
}