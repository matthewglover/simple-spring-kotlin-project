package com.matthewglover.simpleproject.common.routeutils

import com.matthewglover.simpleproject.common.errors.util.ErrorResponse
import com.matthewglover.simpleproject.common.logging.NextHandler
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

object ExceptionHandling {

    suspend fun applyToRoutes(serverRequest: ServerRequest, next: NextHandler): ServerResponse {
        @Suppress("TooGenericExceptionCaught")
        return try {
            next(serverRequest)
        } catch (throwable: Throwable) {
            handleUnexpectedException(throwable)
        }
    }

    private suspend fun handleUnexpectedException(throwable: Throwable): ServerResponse {
        val errors = throwable.message?.let(::setOf) ?: setOf()
        val body = ErrorResponse(
            errorType = throwable.javaClass.simpleName,
            errors = errors
        )

        return ServerResponse
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(body)
    }
}
