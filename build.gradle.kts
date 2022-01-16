import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    // Dependency version management
    `java-platform`

    // Dependency versions
    id("com.github.ben-manes.versions")
    id("org.owasp.dependencycheck")
}

group = "com.matthewglover"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/release") }
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

javaPlatform {
    allowDependencies()
}

dependencies {

    val arrowVersion: String by project
    val glassfishVersion: String by project
    val kotlinImmutableCollectionVersion: String by project
    val liquibaseCoreVersion: String by project
    val mockkVersion: String by project
    val restAssuredVersion: String by project
    val springBootVersion: String by project
    val springDocOpenApiVersion: String by project
    val snodgeVersion: String by project
    val springCloudDependenciesVersion: String by project
    val testContainersVersion: String by project

    constraints {
        // Kotlin
        api("org.jetbrains.kotlinx:kotlinx-collections-immutable:$kotlinImmutableCollectionVersion")

        // Open API
        api("org.springdoc:springdoc-openapi-webflux-ui:$springDocOpenApiVersion")
        api("org.springdoc:springdoc-openapi-kotlin:$springDocOpenApiVersion")

        // Testing
        api("io.mockk:mockk:$mockkVersion")
        api("com.natpryce:snodge:$snodgeVersion")
        api("org.glassfish:javax.json:$glassfishVersion")
        api("io.rest-assured:spring-web-test-client:$restAssuredVersion")

        //Integration Tests
        api("org.liquibase:liquibase-core:$liquibaseCoreVersion")
    }

    // BOM config
    api(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
    api(enforcedPlatform("org.springframework.cloud:spring-cloud-dependencies:$springCloudDependenciesVersion"))
    api(platform("io.arrow-kt:arrow-stack:$arrowVersion"))
    api(platform("org.testcontainers:testcontainers-bom:$testContainersVersion"))
}

// Dependency versions config
fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

tasks.withType<DependencyUpdatesTask> {
    gradleReleaseChannel = "current"
    rejectVersionIf {
        isNonStable(candidate.version)
    }
}
