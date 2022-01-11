package com.matthewglover.simpleproject.common.response

import com.matthewglover.simpleproject.common.errors.ApplicationError
import com.matthewglover.simpleproject.common.errors.JsonDecodingError
import com.matthewglover.simpleproject.common.errors.MissingRequestPayloadError
import com.matthewglover.simpleproject.common.errors.UnexpectedRefiningError
import com.matthewglover.simpleproject.common.errors.UnexpectedRequestDataParsingError
import com.matthewglover.simpleproject.common.errors.UnsupportedMediaTypeError
import com.matthewglover.simpleproject.common.errors.ValidationError
import com.matthewglover.simpleproject.common.errors.ValidationErrors
import org.junit.jupiter.api.Test
import org.springframework.core.codec.DecodingException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.server.UnsupportedMediaTypeStatusException

internal class RequestDataParsingErrorResponseMapperTest {
    @Test
    internal fun `MissingRequestPayloadError responds with 400 Bad Request`() {

        testError(MissingRequestPayloadError)
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<ErrorResponse>().isEqualTo(
                ErrorResponse(
                    errorType = MissingRequestPayloadError.javaClass.simpleName,
                    errors = setOf("No payload in request")
                )
            )
    }

    @Test
    internal fun `UnsupportedMediaTypeError responds with 415 Unsupported Media Type`() {
        val exception = UnsupportedMediaTypeStatusException(MediaType.TEXT_PLAIN, listOf(MediaType.APPLICATION_JSON))
        val error = UnsupportedMediaTypeError(exception)

        testError(error)
            .expectStatus().isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<ErrorResponse>().isEqualTo(
                ErrorResponse(
                    errorType = UnsupportedMediaTypeError::class.java.simpleName,
                    errors = setOf("Content type: `text/plain` not supported.")
                )
            )
    }

    @Test
    internal fun `UnexpectedRequestDataParsingError responds with 500 Internal Server Error`() {
        val exception = RuntimeException("This exception is unexpected")
        val error = UnexpectedRequestDataParsingError(exception)

        testError(error)
            .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<ErrorResponse>().isEqualTo(
                ErrorResponse(
                    errorType = UnexpectedRequestDataParsingError::class.java.simpleName,
                    errors = setOf("This exception is unexpected")
                )
            )
    }

    @Test
    internal fun `ValidationErrors responds with 400 Bad Request`() {
        val error = ValidationErrors(
            setOf(
                ValidationError("Validation error 1"),
                ValidationError("Validation error 2")
            )
        )

        testError(error)
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<ErrorResponse>().isEqualTo(
                ErrorResponse(
                    errorType = ValidationErrors::class.java.simpleName,
                    errors = setOf("Validation error 1", "Validation error 2")
                )
            )
    }

    @Test
    internal fun `JsonDecodingError responds with 400 Bad Request`() {
        val exception = DecodingException("This is the decoding exception message")
        val error = JsonDecodingError(exception)

        testError(error)
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<ErrorResponse>().isEqualTo(
                ErrorResponse(
                    errorType = JsonDecodingError::class.java.simpleName,
                    errors = setOf("This is the decoding exception message")
                )
            )
    }

    @Test
    internal fun `UnexpectedRefiningError responds with 500 Internal Server Error`() {
        val exception = RuntimeException("This is an unexpected refining error")
        val error = UnexpectedRefiningError(exception)

        testError(error)
            .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<ErrorResponse>().isEqualTo(
                ErrorResponse(
                    errorType = UnexpectedRefiningError::class.java.simpleName,
                    errors = setOf("This is an unexpected refining error")
                )
            )
    }

    private fun testError(error: ApplicationError): WebTestClient.ResponseSpec {
        val webClient = setupWebClient(error)

        return webClient
            .get().uri("/test")
            .exchange()
    }

    private fun setupWebClient(error: ApplicationError) =
        WebTestClient
            .bindToRouterFunction(testRoute(error))
            .build()

    private fun testRoute(error: ApplicationError) = coRouter {
        accept(MediaType.APPLICATION_JSON).nest {
            GET("/test") { error.asError() }
        }
    }
}
