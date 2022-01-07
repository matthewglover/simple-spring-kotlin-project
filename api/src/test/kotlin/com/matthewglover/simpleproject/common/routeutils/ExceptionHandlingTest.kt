package com.matthewglover.simpleproject.common.routeutils

import com.matthewglover.simpleproject.common.errors.util.ErrorResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.function.server.coRouter

internal class ExceptionHandlingTest {

    @Test
    internal fun `an unexpected exception returns an internal server error`() {
        val webClient = WebTestClient
            .bindToRouterFunction(throwException(RuntimeException("Boom!")))
            .build()

        webClient
            .get()
            .uri("/error")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<ErrorResponse>().isEqualTo(
                ErrorResponse(
                    errorType = "RuntimeException",
                    errors = setOf("Boom!")
                )
            )
    }

    private fun throwException(throwable: Throwable) = coRouter {
        GET("/error") { throw throwable }

        filter(ExceptionHandling::applyToRoutes)
    }
}
