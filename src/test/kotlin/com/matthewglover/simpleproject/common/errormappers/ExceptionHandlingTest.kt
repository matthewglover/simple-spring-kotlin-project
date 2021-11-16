package com.matthewglover.simpleproject.common.errormappers

import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.server.UnsupportedMediaTypeStatusException

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

    @Test
    internal fun `an expected exception returns the correct error`() {
        val expectedException = UnsupportedMediaTypeStatusException(
            MediaType.TEXT_PLAIN,
            listOf(MediaType.APPLICATION_JSON)
        )

        val webClient = WebTestClient
            .bindToRouterFunction(throwException(expectedException))
            .build()

        webClient
            .get()
            .uri("/error")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<ErrorResponse>().isEqualTo(
                ErrorResponse(
                    errorType = expectedException.javaClass.simpleName,
                    errors = setOf("Content type: `text/plain` not supported.")
                )
            )
    }

    private fun throwException(throwable: Throwable) = coRouter {
        GET("/error") { throw throwable }

        filter(ExceptionHandling::applyToRoutes)
    }
}
