package com.matthewglover.simpleproject.common.response

import com.matthewglover.simpleproject.common.errors.JsonDecodingError
import com.matthewglover.simpleproject.common.errors.MissingRequestPayloadError
import com.matthewglover.simpleproject.common.errors.RequestDataParsingError
import com.matthewglover.simpleproject.common.errors.UnexpectedRefiningError
import com.matthewglover.simpleproject.common.errors.UnexpectedRequestDataParsingError
import com.matthewglover.simpleproject.common.errors.UnsupportedMediaTypeError
import com.matthewglover.simpleproject.common.errors.ValidationErrors
import com.matthewglover.simpleproject.common.errors.messages
import org.springframework.web.reactive.function.server.ServerResponse

suspend fun mapRequestDataParsingError(error: RequestDataParsingError): ServerResponse = when (error) {
    is UnsupportedMediaTypeError -> toErrorBody(error).asUnsupportedMedia()
    is ValidationErrors -> toErrorBody(error).asBadRequest()
    is JsonDecodingError, is MissingRequestPayloadError -> toErrorBody(error).asBadRequest()
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
