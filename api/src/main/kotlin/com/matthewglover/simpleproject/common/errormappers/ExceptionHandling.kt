package com.matthewglover.simpleproject.common.errormappers

import com.matthewglover.simpleproject.common.logging.NextHandler
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.server.UnsupportedMediaTypeStatusException

object ExceptionHandling {

    suspend fun applyToRoutes(serverRequest: ServerRequest, next: NextHandler): ServerResponse {
        @Suppress("TooGenericExceptionCaught")
        return try {
            next(serverRequest)
        } catch (throwable: Throwable) {
            when (throwable) {
                is UnsupportedMediaTypeStatusException -> handleUnsupportedMediaTypeStatusException(throwable)
                else -> { handleUnexpectedException(throwable) }
            }
        }
    }

    private suspend fun handleUnsupportedMediaTypeStatusException(
        exception: UnsupportedMediaTypeStatusException
    ): ServerResponse {
        val errorMessage = "Content type: `${exception.contentType ?: ""}` not supported."
        val body = ErrorResponse(
            errorType = exception.javaClass.simpleName,
            errors = setOf(errorMessage)
        )

        return ServerResponse
            .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(body)
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
