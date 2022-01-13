@file:Suppress("MaxLineLength")
package com.matthewglover.simpleproject.common.requestinput

import arrow.core.Either
import arrow.core.right
import com.matthewglover.simpleproject.errors.InvalidPathVariableNameError
import com.matthewglover.simpleproject.errors.InvalidPathVariableValue
import com.matthewglover.simpleproject.errors.RequestDataError
import io.mockk.slot
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.coRouter

class RequestPathTest {
    @Test
    internal fun `returns a Right of  parsed String for valid path variable name`() {
        val slot = slot<Either<RequestDataError, String>>()
        val testClient = setupWebClient { request ->
            slot.captured = request.parsePathVariable("valid_name")
        }

        testClient
            .get().uri("/test/string_value")
            .exchange()
            .expectStatus().isOk

        assertEquals("string_value".right(), slot.captured)
    }

    @Test
    internal fun `returns a Right of  parsed Int for valid path variable name`() {
        val slot = slot<Either<RequestDataError, Int>>()
        val testClient = setupWebClient { request ->
            slot.captured = request.parsePathVariable("valid_name") { it.toInt() }
        }

        testClient
            .get().uri("/test/1123")
            .exchange()
            .expectStatus().isOk

        assertEquals(1123.right(), slot.captured)
    }

    @Test
    internal fun `does not throw and returns a Left of InvalidVariableNameError when variable name not in path`() {
        val slot = slot<Either<RequestDataError, String>>()
        val testClient = setupWebClient { request ->
            slot.captured = request.parsePathVariable("invalid_name")
        }

        testClient
            .get().uri("/test/string_value")
            .exchange()
            .expectStatus().isOk

        assertTrue(slot.captured.isLeft())

        slot.captured.tapLeft { error ->
            assertInstanceOf(InvalidPathVariableNameError::class.java, error)
            assertEquals("No path variable with name \"invalid_name\" available", error.message)
        }
    }

    @Test
    internal fun `does not throw and returns a Left of InvalidPathVariableValue when variable cannot be converted to Int`() {
        val slot = slot<Either<RequestDataError, Int>>()
        val testClient = setupWebClient { request ->
            slot.captured = request.parsePathVariable("valid_name") { it.toInt() }
        }

        testClient
            .get().uri("/test/string_value")
            .exchange()
            .expectStatus().isOk

        assertTrue(slot.captured.isLeft())

        slot.captured.tapLeft { error ->
            assertInstanceOf(InvalidPathVariableValue::class.java, error)
            assertEquals("NumberFormatException: For input string: \"string_value\"", error.message)
        }
    }

    private fun setupWebClient(runnable: (ServerRequest) -> Unit) =
        WebTestClient
            .bindToRouterFunction(testRoute(runnable))
            .build()

    private fun testRoute(runnable: (ServerRequest) -> Unit) = coRouter {
        GET("/test/{valid_name}") { request ->
            @Suppress("SwallowedException")
            try {
                runnable(request)
                ServerResponse.ok().buildAndAwait()
            } catch (throwable: Throwable) {
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).buildAndAwait()
            }
        }
    }
}
