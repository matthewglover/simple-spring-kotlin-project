package com.matthewglover.simpleproject.common.response

import com.matthewglover.simpleproject.errors.InvalidPathVariableNameError
import com.matthewglover.simpleproject.errors.InvalidPathVariableValue
import com.matthewglover.simpleproject.errors.JsonDecodingError
import com.matthewglover.simpleproject.errors.MissingRequestPayloadError
import com.matthewglover.simpleproject.errors.RequestDataParsingError
import com.matthewglover.simpleproject.errors.UnexpectedRefiningError
import com.matthewglover.simpleproject.errors.UnexpectedRequestDataParsingError
import com.matthewglover.simpleproject.errors.UnsupportedMediaTypeError
import com.matthewglover.simpleproject.errors.ValidationErrors
import com.matthewglover.simpleproject.errors.messages
import org.springframework.web.reactive.function.server.ServerResponse

suspend fun mapRequestDataParsingError(error: RequestDataParsingError): ServerResponse = when (error) {
    is UnsupportedMediaTypeError -> toErrorBody(error).asUnsupportedMedia()
    is ValidationErrors -> toErrorBody(error).asBadRequest()
    is JsonDecodingError, MissingRequestPayloadError, is InvalidPathVariableNameError, is InvalidPathVariableValue -> {
        toErrorBody(error).asBadRequest()
    }
    is UnexpectedRequestDataParsingError, is UnexpectedRefiningError -> toErrorBody(error).asInternalServerError()
}

private fun toErrorBody(error: ValidationErrors) = ErrorResponse(
    errorType = error.javaClass.simpleName,
    errors = error.messages()
)

private fun toErrorBody(error: RequestDataParsingError) = ErrorResponse(
    errorType = error.javaClass.simpleName,
    errors = setOf(error.message)
)
