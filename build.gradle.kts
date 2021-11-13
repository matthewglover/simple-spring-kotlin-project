import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN
import org.jlleitschuh.gradle.ktlint.tasks.KtLintCheckTask
import org.springframework.cloud.contract.verifier.config.TestFramework
import org.springframework.cloud.contract.verifier.config.TestMode


plugins {
    // Spring
    id("org.springframework.boot")
    id("org.springframework.cloud.contract")

    // Kotlin
    kotlin("jvm")
    kotlin("plugin.spring")

    // Static Analysis
    id("io.gitlab.arturbosch.detekt")
    id("org.jlleitschuh.gradle.ktlint")

    // Dependency versions
    id("com.github.ben-manes.versions")
    id("org.owasp.dependencycheck")

    // Publish
    id("maven-publish")
}

group = "com.matthewglover"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/release") }
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

// Configure handlerTest
// See https://docs.gradle.org/current/userguide/java_testing.html#sec:configuring_java_integration_tests

sourceSets {
    create("handlerTest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val handlerTestImplementation by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

configurations["handlerTestImplementation"].extendsFrom(configurations.runtimeOnly.get())

val handlerTest = task<Test>("handlerTest") {
    description = "Runs handler tests."
    group = "verification"

    testClassesDirs = sourceSets["handlerTest"].output.classesDirs
    classpath = sourceSets["handlerTest"].runtimeClasspath
    shouldRunAfter("test")
}

tasks.check { dependsOn(handlerTest) }

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
    // BOM config
    implementation(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
    implementation(enforcedPlatform("org.springframework.cloud:spring-cloud-dependencies:$springCloudDependenciesVersion"))
    implementation(platform("io.arrow-kt:arrow-stack:$arrowVersion"))

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")

    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:$kotlinImmutableCollectionVersion")

    // Utility libraries
    implementation("io.arrow-kt:arrow-core")

    // Open API
    implementation("org.springdoc:springdoc-openapi-webflux-ui:$springDocOpenApiVersion")
    implementation("org.springdoc:springdoc-openapi-kotlin:$springDocOpenApiVersion")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
        exclude(module = "mockito-core")
    }
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("com.natpryce:snodge:$snodgeVersion")
    testRuntimeOnly("org.glassfish:javax.json:$glassfishVersion")
    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-verifier")
    testImplementation("org.springframework.cloud:spring-cloud-contract-spec-kotlin")
    testImplementation(kotlin("script-runtime"))
    testImplementation("io.rest-assured:spring-web-test-client:$restAssuredVersion")

    // Plugin configuration
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        // NOTE: unrestricted-builder-inference flag will be default in kotlin 1.6
        // See: https://kotlinlang.org/docs/whatsnew1530.html#eliminating-builder-inference-restrictions
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xself-upper-bound-inference")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// KtLint config
ktlint {
    reporters {
        reporter(PLAIN)
        reporter(HTML)
    }
    filter {
        exclude("**/generated/**")
        exclude("**/generated-test-sources/**")
    }
}

tasks.withType<KtLintCheckTask>() {
    mustRunAfter("generateContractTests")
}

// Detekt config
detekt {
    reports {
        xml {
            enabled = false
        }
        txt {
            enabled = false
        }
        html {
            enabled = true
            destination = file("build/reports/detekt/detekt.html")
        }
    }
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

configurations.all {
    //   spring-boot-starter-logging module causes a conflict with spring-boot-starter-log4j2
    //   As spring-boot-starter-logging is included by multiple dependencies:
    //   spring-boot-starter-webflux, spring-boot-starter-actuator, spring-boot-starter-validation
    //   we globally exclude it here, rather than in each dependency
    exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
}

contracts {
    testFramework.set(TestFramework.JUNIT5)
    testMode.set(TestMode.WEBTESTCLIENT)
    packageWithBaseClasses.set("com.matthewglover.simpleproject")
}

tasks {
    contractTest {
        useJUnitPlatform()
        systemProperty("spring.profiles.active", "gradle")
        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
        }
        afterSuite(
            KotlinClosure2({ desc: TestDescriptor, result: TestResult ->
                if (desc.parent == null) {
                    if (result.testCount == 0L) {
                        throw IllegalStateException("No tests were found. Failing the build")
                    } else {
                        println("Results: (${result.testCount} tests, ${result.successfulTestCount} successes, ${result.failedTestCount} failures, ${result.skippedTestCount} skipped)")
                    }
                } else { /* Nothing to do here */ }
            })
        )
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks.named("bootJar"))

            artifact(tasks.named("verifierStubsJar"))
        }
    }
}
