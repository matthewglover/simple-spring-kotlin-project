plugins {
    id("org.liquibase.gradle")
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    val liquibaseCoreVersion: String by project
    val postgresVersion: String by project
    val snakeYamlVersion: String by project
    val jaxbApiVersion: String by project
    val picocliVersion: String by project

    // Liquibase (note these don't work out of the box with enforced platform approach, so dependencies defined inline)
    liquibaseRuntime("org.liquibase:liquibase-core:$liquibaseCoreVersion")
    liquibaseRuntime("org.postgresql:postgresql:$postgresVersion")
    liquibaseRuntime("org.yaml:snakeyaml:$snakeYamlVersion")
    liquibaseRuntime("javax.xml.bind:jaxb-api:$jaxbApiVersion")
    liquibaseRuntime("info.picocli:picocli:$picocliVersion")
}

liquibase {
    // Note this assumes a dev setup - url, username and password of non-local systems shouldn't be configured here
    activities.register("main") {
        this.arguments = mapOf(
            "logLevel" to "info",
            "classpath" to "$projectDir",
            "changeLogFile" to "src/main/resources/liquibase/liquibase-changelog.yaml",
            "url" to "jdbc:postgresql://localhost:5432/devdb",
            "username" to "devdb",
            "password" to "devpassword"
        )
    }
}

tasks.register("dev") {
    // depend on the liquibase status task
    dependsOn("update")
}
