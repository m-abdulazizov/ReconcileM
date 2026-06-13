dependencies {
    api(project(":reconcilem-core"))

    implementation("org.springframework:spring-jdbc:6.2.2")

    testRuntimeOnly("com.h2database:h2:2.2.224")
}