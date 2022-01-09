package com.matthewglover.simpleproject.common.routeutils

import com.matthewglover.simpleproject.common.logging.NextHandler
import com.matthewglover.simpleproject.common.response.ErrorResponse
import com.matthewglover.simpleproject.common.response.asInternalServerError
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

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

        return ErrorResponse(
            errorType = throwable.javaClass.simpleName,
            errors = errors
        ).asInternalServerError()
    }
}
