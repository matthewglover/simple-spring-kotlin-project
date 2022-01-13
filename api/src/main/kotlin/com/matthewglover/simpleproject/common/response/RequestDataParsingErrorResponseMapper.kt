package com.matthewglover.simpleproject.common.response

import com.matthewglover.simpleproject.errors.InvalidPathVariableNameError
import com.matthewglover.simpleproject.errors.InvalidPathVariableValue
import com.matthewglover.simpleproject.errors.MissingRequestPayloadError
import com.matthewglover.simpleproject.errors.RequestDataError
import com.matthewglover.simpleproject.errors.UnexpectedRequestDataError
import com.matthewglover.simpleproject.errors.UnsupportedMediaTypeError
import org.springframework.web.reactive.function.server.ServerResponse

suspend fun mapRequestDataParsingError(error: RequestDataError): ServerResponse = when (error) {
    is UnsupportedMediaTypeError -> toErrorBody(error).asUnsupportedMedia()
    is MissingRequestPayloadError, is InvalidPathVariableNameError, is InvalidPathVariableValue -> {
        toErrorBody(error).asBadRequest()
    }
    is UnexpectedRequestDataError -> toErrorBody(error).asInternalServerError()
}

private fun toErrorBody(error: RequestDataError) = ErrorResponse(
    errorType = error.javaClass.simpleName,
    errors = setOf(error.message)
)
