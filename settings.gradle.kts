rootProject.name = "simple-project"

pluginManagement {

    plugins {
        val springBootVersion: String by settings
        val springBootDependencyManagementVersion: String by settings
        val kotlinVersion: String by settings
        val detektVersion: String by settings
        val ktlintVersion: String by settings

        // Spring
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version springBootDependencyManagementVersion

        // Kotlin
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion

        // Static Analysis
        id("io.gitlab.arturbosch.detekt") version detektVersion
        id("org.jlleitschuh.gradle.ktlint") version ktlintVersion
    }
}