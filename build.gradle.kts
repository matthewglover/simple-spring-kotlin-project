import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN

plugins {
    // Spring
    id("org.springframework.boot")

    // Kotlin
    kotlin("jvm")
    kotlin("plugin.spring")

    // Static Analysis
    id("io.gitlab.arturbosch.detekt")
    id("org.jlleitschuh.gradle.ktlint")

    // Dependency versions
    id("com.github.ben-manes.versions")
    id("org.owasp.dependencycheck")
}

group = "com.matthewglover"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

val springBootVersion: String by project
val detektVersion: String by project

dependencies {
    // BOM config
    implementation(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")

    // Plugin configuration
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
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
