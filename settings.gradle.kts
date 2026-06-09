pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}

rootProject.name = "reconcilem"

include(
    "reconcilem-core",
    "reconcilem-csv",
    "reconcilem-jdbc",
    "reconcilem-spring-boot-starter",
    "reconcilem-test",
    "reconcilem-example-app"
)