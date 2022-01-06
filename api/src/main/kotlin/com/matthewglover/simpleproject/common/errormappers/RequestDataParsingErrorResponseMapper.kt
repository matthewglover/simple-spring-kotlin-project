package com.matthewglover.simpleproject.common.errormappers

import com.matthewglover.simpleproject.common.errors.JsonDecodingError
import com.matthewglover.simpleproject.common.errors.MissingRequestPayloadError
import com.matthewglover.simpleproject.common.errors.RequestDataParsingError
import com.matthewglover.simpleproject.common.errors.UnexpectedRefiningError
import com.matthewglover.simpleproject.common.errors.ValidationErrors
import com.matthewglover.simpleproject.common.errors.messages
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

object RequestDataParsingErrorResponseMapper {

    suspend fun map(error: RequestDataParsingError): ServerResponse {
        val errorResponse = when (error) {
            is MissingRequestPayloadError -> missingRequestPayloadErrorResponse()
            is JsonDecodingError -> decodingErrorResponse(error)
            is ValidationErrors -> validationErrorResponse(error)
            is UnexpectedRefiningError -> refiningErrorResponse(error)
        }

        return badRequestResponse(errorResponse)
    }

    private fun missingRequestPayloadErrorResponse() = ErrorResponse(
        errorType = MissingRequestPayloadError.javaClass.simpleName,
        errors = setOf(MissingRequestPayloadError.message)
    )

    private fun decodingErrorResponse(error: JsonDecodingError) = ErrorResponse(
        errorType = error.javaClass.simpleName,
        errors = setOf(error.decodingException.localizedMessage)
    )

    private fun validationErrorResponse(error: ValidationErrors) =
        ErrorResponse(errorType = error.javaClass.simpleName, errors = error.messages())

    private fun refiningErrorResponse(error: UnexpectedRefiningError) =
        ErrorResponse(errorType = error.javaClass.simpleName, errors = setOf(error.throwable.localizedMessage))

    private suspend fun badRequestResponse(errorResponse: ErrorResponse) =
        ServerResponse.badRequest()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(errorResponse)
}
