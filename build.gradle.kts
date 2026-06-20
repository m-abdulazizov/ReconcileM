import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    base
    id("org.springframework.boot") version "3.5.10" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

group = "io.github.mabdulazizov"
version = "0.1.0-SNAPSHOT"

subprojects {
    group = rootProject.group
    version = rootProject.version
    description = when (name) {
        "reconcilem-core" -> "Core reconciliation engine, domain model, rules, and normalizers"
        "reconcilem-csv" -> "CSV reader and reconciliation result writer for ReconcileM"
        "reconcilem-jdbc" -> "JDBC reader and result persistence for ReconcileM"
        "reconcilem-spring-boot-starter" -> "Spring Boot auto-configuration for ReconcileM"
        "reconcilem-test" -> "Test fixtures and assertions for ReconcileM users"
        "reconcilem-example-app" -> "Runnable ReconcileM demo application"
        else -> "ReconcileM module"
    }

    apply(plugin = "java-library")

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }

        withSourcesJar()
        withJavadocJar()
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    dependencies {
        add("testImplementation", "org.junit.jupiter:junit-jupiter:5.12.2")
        add("testImplementation", "org.assertj:assertj-core:3.26.3")
        add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher:1.12.2")
    }

    if (name != "reconcilem-example-app") {
        apply(plugin = "maven-publish")

        configure<PublishingExtension> {
            publications {
                create<MavenPublication>("mavenJava") {
                    from(components["java"])

                    pom {
                        name.set(project.name)
                        description.set(project.description)
                        url.set("https://github.com/mabdulazizov/ReconcileM")

                        scm {
                            connection.set("scm:git:https://github.com/mabdulazizov/ReconcileM.git")
                            developerConnection.set("scm:git:ssh://git@github.com/mabdulazizov/ReconcileM.git")
                            url.set("https://github.com/mabdulazizov/ReconcileM")
                        }

                        developers {
                            developer {
                                id.set("mabdulazizov")
                                name.set("Muhammadjon Abdulazizov")
                            }
                        }
                    }
                }
            }

            repositories {
                maven {
                    name = "localBuild"
                    url = uri(rootProject.layout.buildDirectory.dir("repo").get().asFile)
                }
            }
        }
    }
}
