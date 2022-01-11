package com.matthewglover.simpleproject.common.response

import com.matthewglover.simpleproject.common.errors.ReactorContextReadError
import com.matthewglover.simpleproject.common.errors.RequestLoggingContextReadError
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.expectBody

class ContextErrorResponseMapperTest {
    @Test
    internal fun `ReactorContextReadError responds with 500 Internal Server Error`() {

        testError(ReactorContextReadError)
            .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<ErrorResponse>().isEqualTo(
                ErrorResponse(
                    errorType = ReactorContextReadError.javaClass.simpleName,
                    errors = setOf("Unexpectedly failed to read Reactor Context")
                )
            )
    }

    @Test
    internal fun `RequestLoggingContextReadError responds with 500 Internal Server Error`() {

        testError(RequestLoggingContextReadError)
            .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<ErrorResponse>().isEqualTo(
                ErrorResponse(
                    errorType = RequestLoggingContextReadError.javaClass.simpleName,
                    errors = setOf("Unexpectedly failed to read Request Logging Context")
                )
            )
    }
}
