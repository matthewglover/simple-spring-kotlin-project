package com.matthewglover.simpleproject.common.response

import com.matthewglover.simpleproject.errors.ContextError
import com.matthewglover.simpleproject.errors.ReactorContextReadError
import com.matthewglover.simpleproject.errors.RequestLoggingContextReadError
import org.springframework.web.reactive.function.server.ServerResponse

suspend fun mapContextError(contextError: ContextError): ServerResponse = when (contextError) {
    ReactorContextReadError, RequestLoggingContextReadError -> toErrorBody(contextError).asInternalServerError()
}

private fun toErrorBody(error: ContextError) = ErrorResponse(
    errorType = error.javaClass.simpleName,
    errors = setOf(error.message)
)
