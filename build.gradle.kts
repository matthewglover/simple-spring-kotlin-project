plugins {
    `java-platform`
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

val springBootVersion: String by project
val springDocOpenApiVersion: String by project
val detektVersion: String by project
val arrowVersion: String by project
val mockkVersion: String by project
val snodgeVersion: String by project
val glassfishVersion: String by project
val kotlinImmutableCollectionVersion: String by project
val springCloudDependenciesVersion: String by project
val restAssuredVersion: String by project

dependencies {
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
    }

    // BOM config
    api(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
    api(enforcedPlatform("org.springframework.cloud:spring-cloud-dependencies:$springCloudDependenciesVersion"))
    api(platform("io.arrow-kt:arrow-stack:$arrowVersion"))
}
