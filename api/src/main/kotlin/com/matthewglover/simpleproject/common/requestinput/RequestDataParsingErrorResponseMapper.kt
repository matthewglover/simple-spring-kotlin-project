package com.matthewglover.simpleproject.common.requestinput

import com.matthewglover.simpleproject.common.errors.JsonDecodingError
import com.matthewglover.simpleproject.common.errors.MissingRequestPayloadError
import com.matthewglover.simpleproject.common.errors.RequestDataParsingError
import com.matthewglover.simpleproject.common.errors.UnexpectedRefiningError
import com.matthewglover.simpleproject.common.errors.UnexpectedRequestDataParsingError
import com.matthewglover.simpleproject.common.errors.UnsupportedMediaTypeError
import com.matthewglover.simpleproject.common.errors.ValidationErrors
import com.matthewglover.simpleproject.common.errors.messages
import com.matthewglover.simpleproject.common.errors.util.ErrorResponse
import com.matthewglover.simpleproject.common.errors.util.asBadRequest
import com.matthewglover.simpleproject.common.errors.util.asInternalServerError
import com.matthewglover.simpleproject.common.errors.util.asUnsupportedMedia
import org.springframework.web.reactive.function.server.ServerResponse

object RequestDataParsingErrorResponseMapper {

    suspend fun map(error: RequestDataParsingError): ServerResponse = when (error) {
        is MissingRequestPayloadError -> toErrorBody(MissingRequestPayloadError).asBadRequest()
        is UnsupportedMediaTypeError -> toErrorBody(error).asUnsupportedMedia()
        is UnexpectedRequestDataParsingError -> toErrorBody(error).asInternalServerError()
        is ValidationErrors -> toErrorBody(error).asBadRequest()
        is JsonDecodingError, is UnexpectedRefiningError -> toErrorBody(error).asBadRequest()
    }

    private fun toErrorBody(error: ValidationErrors) = ErrorResponse(
        errorType = error.javaClass.simpleName,
        errors = error.messages()
    )

    private fun toErrorBody(error: RequestDataParsingError) = ErrorResponse(
        errorType = error.javaClass.simpleName,
        errors = setOf(error.message)
    )
}
