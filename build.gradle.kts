import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN

plugins {
    // Spring
    id("org.springframework.boot") version "2.5.4"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"

    // Kotlin
    kotlin("jvm") version "1.5.21"
    kotlin("plugin.spring") version "1.5.21"

    // Static Analysis
    id("io.gitlab.arturbosch.detekt") version "1.18.1"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
}

group = "com.matthewglover"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
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

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.18.1")
}

ktlint {
    reporters {
        reporter(PLAIN)
        reporter(HTML)
    }
}

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
