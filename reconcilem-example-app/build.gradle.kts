plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(project(":reconcilem-core"))
    implementation(project(":reconcilem-csv"))
    implementation(project(":reconcilem-spring-boot-starter"))

    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    runtimeOnly("com.h2database:h2")
    testRuntimeOnly("com.h2database:h2")
}
