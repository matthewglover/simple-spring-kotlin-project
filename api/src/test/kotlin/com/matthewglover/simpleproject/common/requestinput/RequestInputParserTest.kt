package com.matthewglover.simpleproject.common.requestinput

import com.fasterxml.jackson.databind.ObjectMapper
import com.matthewglover.simpleproject.features.users.NewUser
import com.natpryce.snodge.json.defaultJsonMutagens
import com.natpryce.snodge.json.forStrings
import com.natpryce.snodge.mutants
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.coRouter
import java.util.stream.Stream
import kotlin.random.Random

internal class RequestInputParserTest {

    companion object {
        private val objectMapper = ObjectMapper()

        @JvmStatic
        fun provideJsonFuzz(): Stream<Arguments> {
            val random = Random.Default
            val validJson = objectMapper.writeValueAsString(NewUser("blah"))

            return random.mutants(defaultJsonMutagens().forStrings(), 100, validJson)
                .map { Arguments.of(it) }
                .stream()
        }
    }

    @ParameterizedTest
    @MethodSource("provideJsonFuzz")
    internal fun `does not error for a valid json payload`(json: String) {
        val testClient = setupWebClient<NewUser>()

        testClient
            .post().uri("/test")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(json)
            .exchange()
            .expectStatus().isOk
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            """{"username": "blah""""",
            """"username": "blah"}"""",
            """{username: blah}"""",
            ""
        ]
    )
    internal fun `does not error for an invalid json payload`(json: String) {
        val testClient = setupWebClient<NewUser>()

        testClient
            .post().uri("/test")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(json)
            .exchange()
            .expectStatus().isOk
    }

    @Test
    internal fun `does not error for a missing payload`() {
        val testClient = setupWebClient<NewUser>()

        testClient
            .post().uri("/test")
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
    }

    private inline fun <reified T : Any> setupWebClient(): WebTestClient {
        return WebTestClient
            .bindToRouterFunction(testRoute<T>())
            .build()
    }

    private inline fun <reified T : Any> testRoute() = coRouter {
        accept(MediaType.APPLICATION_JSON).nest {
            POST("/test") { request ->
                @Suppress("SwallowedException")
                try {
                    RequestInputParser.parseBody<T>(request)
                    ServerResponse.ok().buildAndAwait()
                } catch (throwable: Throwable) {
                    ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).buildAndAwait()
                }
            }
        }
    }
}
