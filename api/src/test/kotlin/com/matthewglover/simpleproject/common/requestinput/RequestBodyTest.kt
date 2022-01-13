package com.matthewglover.simpleproject.common.requestinput

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.databind.ObjectMapper
import com.matthewglover.simpleproject.errors.ApplicationError
import com.matthewglover.simpleproject.errors.JsonDecodingError
import com.matthewglover.simpleproject.errors.MissingRequestPayloadError
import com.matthewglover.simpleproject.errors.ValidationError
import com.matthewglover.simpleproject.errors.ValidationErrors
import com.natpryce.snodge.json.defaultJsonMutagens
import com.natpryce.snodge.json.forStrings
import com.natpryce.snodge.mutants
import io.mockk.CapturingSlot
import io.mockk.slot
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
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
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import kotlin.random.Random

internal class RequestBodyTest {

    data class RawType(
        @get: NotBlank(message = "username required")
        val username: String?,

        @get: Pattern(regexp = ".+@.+\\.[a-z]+", message = "email must be valid")
        val email: String?
    ) : Refineable<RefinedType> {

        override fun unsafeRefine() =
            RefinedType(username = username!!, email = email!!)
    }

    data class RefinedType(
        val username: String,
        val email: String
    )

    @Test
    internal fun `parses and refines valid input`() {
        val slot = slot<Either<ApplicationError, RefinedType>>()
        val testClient = setupWebClient<RawType, RefinedType>(slot)

        testClient
            .post().uri("/test")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"username": "blah", "email": "blah@blah.com"}"""")
            .exchange()
            .expectStatus().isOk

        assertEquals(RefinedType("blah", "blah@blah.com").right(), slot.captured)
    }

    @ParameterizedTest
    @MethodSource("provideJsonFuzz")
    internal fun `does not error for a valid json payload`(json: String) {
        val testClient = setupWebClient<RawType, RefinedType>()

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
    internal fun `does not throw and returns a Left of JsonDecodingError for invalid json payload`(json: String) {
        val slot = slot<Either<ApplicationError, RefinedType>>()
        val testClient = setupWebClient<RawType, RefinedType>(slot)

        testClient
            .post().uri("/test")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(json)
            .exchange()
            .expectStatus().isOk

        assertTrue(slot.captured.isLeft())

        slot.captured.tapLeft {
            assertInstanceOf(JsonDecodingError::class.java, it)
        }
    }

    @Test
    internal fun `does not throw and returns a Left of MissingRequestPayloadError when the payload is missing`() {
        val slot = slot<Either<ApplicationError, RefinedType>>()
        val testClient = setupWebClient<RawType, RefinedType>(slot)

        testClient
            .post().uri("/test")
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk

        assertTrue(slot.captured.isLeft())

        slot.captured.tapLeft {
            assertInstanceOf(MissingRequestPayloadError::class.java, it)
        }
    }

    @Test
    internal fun `returns a Left of ValidationErrors when there are validation errors`() {
        val slot = slot<Either<ApplicationError, RefinedType>>()
        val testClient = setupWebClient<RawType, RefinedType>(slot)

        testClient
            .post().uri("/test")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"username": null, "email": "invalid email"}"""")
            .exchange()
            .expectStatus().isOk

        val expected = ValidationErrors(
            setOf(
                ValidationError("username required"),
                ValidationError("email must be valid")
            )
        ).left()

        assertEquals(expected, slot.captured)
    }

    private inline fun <reified UnrefinedType : Refineable<RefinedType>, reified RefinedType : Any> setupWebClient(
        slot: CapturingSlot<Either<ApplicationError, RefinedType>> = slot()
    ) =
        WebTestClient
            .bindToRouterFunction(testRoute<UnrefinedType, RefinedType>(slot))
            .build()

    private inline fun <reified UnrefinedType : Refineable<RefinedType>, reified RefinedType : Any> testRoute(
        slot: CapturingSlot<Either<ApplicationError, RefinedType>>
    ) = coRouter {
        accept(MediaType.APPLICATION_JSON).nest {
            POST("/test") { request ->
                @Suppress("SwallowedException")
                try {
                    val result = request.parseBody<UnrefinedType, RefinedType>()
                    slot.captured = result
                    ServerResponse.ok().buildAndAwait()
                } catch (throwable: Throwable) {
                    ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).buildAndAwait()
                }
            }
        }
    }

    companion object {
        private val objectMapper = ObjectMapper()

        @JvmStatic
        fun provideJsonFuzz(): Stream<Arguments> {
            val random = Random.Default
            val validJson = objectMapper.writeValueAsString(RawType("blah", "blah@blah.com"))

            return random.mutants(defaultJsonMutagens().forStrings(), 100, validJson)
                .map { Arguments.of(it) }
                .stream()
        }
    }
}
