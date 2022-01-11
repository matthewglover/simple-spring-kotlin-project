package com.matthewglover.simpleproject.common.response

import com.matthewglover.simpleproject.common.errors.ContextError
import com.matthewglover.simpleproject.common.errors.ReactorContextReadError
import com.matthewglover.simpleproject.common.errors.RequestLoggingContextReadError
import org.springframework.web.reactive.function.server.ServerResponse

suspend fun mapContextError(contextError: ContextError): ServerResponse = when (contextError) {
    ReactorContextReadError, RequestLoggingContextReadError -> toErrorBody(contextError).asInternalServerError()
}

private fun toErrorBody(error: ContextError) = ErrorResponse(
    errorType = error.javaClass.simpleName,
    errors = setOf(error.message)
)
