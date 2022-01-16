package com.matthewglover.simpleproject.utils

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.TestPropertySourceUtils
import org.testcontainers.containers.PostgreSQLContainer

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [TestDatabaseConfig.DockerPostgresDataSourceInitializer::class])
abstract class TestDatabaseConfig {

    companion object {
        val postgresDBContainer: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:latest")

        init {
            postgresDBContainer.start()
        }
    }

    class DockerPostgresDataSourceInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                applicationContext,
                "spring.liquibase.driver-class-name=org.postgresql.Driver",
                "spring.liquibase.change-log=classpath:liquibase/liquibase-changelog.yaml",
                "spring.liquibase.enabled=true",
                "spring.liquibase.contexts=test",
                "spring.liquibase.drop-first=false",
                "spring.liquibase.url=" + postgresDBContainer.jdbcUrl,
                "spring.liquibase.user=" + postgresDBContainer.username,
                "spring.liquibase.password=" + postgresDBContainer.password,
                "spring.r2dbc.url=" + postgresDBContainer.r2dbcUrl(),
                "spring.r2dbc.username=" + postgresDBContainer.username,
                "spring.r2dbc.password=" + postgresDBContainer.password,
            )
        }

        private fun PostgreSQLContainer<*>.r2dbcUrl(): String {
            return buildString {
                append("r2dbc:postgresql://$containerIpAddress:")
                append(getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT))
                append("/$databaseName")
            }
        }
    }
}
