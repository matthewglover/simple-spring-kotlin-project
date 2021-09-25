rootProject.name = "simple-project"

pluginManagement {

    plugins {
        val springBootVersion: String by settings
        val kotlinVersion: String by settings
        val detektVersion: String by settings
        val ktlintVersion: String by settings
        val dependencyVersionsVersion: String by settings
        val dependencyCheckVersion: String by settings

        // Spring
        id("org.springframework.boot") version springBootVersion

        // Kotlin
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion

        // Static Analysis
        id("io.gitlab.arturbosch.detekt") version detektVersion
        id("org.jlleitschuh.gradle.ktlint") version ktlintVersion

        // Dependency versions
        id("com.github.ben-manes.versions") version dependencyVersionsVersion
        id("org.owasp.dependencycheck") version dependencyCheckVersion
    }
}
