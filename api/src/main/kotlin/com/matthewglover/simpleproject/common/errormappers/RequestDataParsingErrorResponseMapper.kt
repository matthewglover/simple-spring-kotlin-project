package com.matthewglover.simpleproject.common.errormappers

import com.matthewglover.simpleproject.common.errors.JsonDecodingError
import com.matthewglover.simpleproject.common.errors.MissingRequestPayloadError
import com.matthewglover.simpleproject.common.errors.RequestDataParsingError
import com.matthewglover.simpleproject.common.errors.UnexpectedRefiningError
import com.matthewglover.simpleproject.common.errors.ValidationErrors
import com.matthewglover.simpleproject.common.errors.messages
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

object RequestDataParsingErrorResponseMapper {
    suspend fun map(error: RequestDataParsingError): ServerResponse =
        when (error) {
            is MissingRequestPayloadError -> missingRequestPayloadError()
            is JsonDecodingError -> decodingErrorResponse(error)
            is ValidationErrors -> validationErrorResponse(error)
            is UnexpectedRefiningError -> refiningErrorResponse(error)
        }

    private suspend fun missingRequestPayloadError(): ServerResponse {
        val errorResponse = ErrorResponse(
            errorType = MissingRequestPayloadError.javaClass.simpleName,
            errors = setOf(MissingRequestPayloadError.message)
        )

        return badRequestResponse(errorResponse)
    }

    private suspend fun decodingErrorResponse(error: JsonDecodingError): ServerResponse {
        val errorResponse =
            ErrorResponse(
                errorType = error.javaClass.simpleName,
                errors = setOf(error.decodingException.localizedMessage)
            )

        return badRequestResponse(errorResponse)
    }

    private suspend fun validationErrorResponse(error: ValidationErrors): ServerResponse {
        val errorResponse =
            ErrorResponse(errorType = error.javaClass.simpleName, errors = error.messages())

        return badRequestResponse(errorResponse)
    }

    private suspend fun refiningErrorResponse(error: UnexpectedRefiningError): ServerResponse {
        val errorResponse =
            ErrorResponse(errorType = error.javaClass.simpleName, errors = setOf(error.throwable.localizedMessage))

        return internalServerErrorResponse(errorResponse)
    }

    private suspend fun <T : Any> badRequestResponse(t: T) =
        ServerResponse.badRequest()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(t)

    private suspend fun <T : Any> internalServerErrorResponse(t: T) =
        ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(t)
}
