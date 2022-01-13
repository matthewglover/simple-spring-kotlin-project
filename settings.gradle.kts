rootProject.name = "simple-project"

include("api")
include("database")

pluginManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://repo.spring.io/release") }
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
        gradlePluginPortal()
    }

    plugins {
        val springBootVersion: String by settings
        val kotlinVersion: String by settings
        val detektVersion: String by settings
        val ktlintVersion: String by settings
        val dependencyVersionsVersion: String by settings
        val dependencyCheckVersion: String by settings
        val springCloudContractVersion: String by settings
        val liquibaseGradlePluginVersion: String by settings

        // Spring
        id("org.springframework.boot") version springBootVersion
        id("org.springframework.cloud.contract") version springCloudContractVersion

        // Kotlin
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion

        // Static Analysis
        id("io.gitlab.arturbosch.detekt") version detektVersion
        id("org.jlleitschuh.gradle.ktlint") version ktlintVersion

        // Dependency versions
        id("com.github.ben-manes.versions") version dependencyVersionsVersion
        id("org.owasp.dependencycheck") version dependencyCheckVersion

        // Liquibase
        id("org.liquibase.gradle") version liquibaseGradlePluginVersion
    }
}
