package com.matthewglover.simpleproject.common.response

import com.matthewglover.simpleproject.errors.DatabaseFieldMappingError
import com.matthewglover.simpleproject.errors.ExternalDataParsingError
import com.matthewglover.simpleproject.errors.JsonDecodingError
import com.matthewglover.simpleproject.errors.UnexpectedDataParsingError
import com.matthewglover.simpleproject.errors.UnexpectedRefiningError
import com.matthewglover.simpleproject.errors.UnmappableDatabaseFieldType
import com.matthewglover.simpleproject.errors.ValidationErrors
import com.matthewglover.simpleproject.errors.messages
import org.springframework.web.reactive.function.server.ServerResponse

suspend fun mapExternalDataConversion(error: ExternalDataParsingError): ServerResponse =
    when (error) {
        is JsonDecodingError -> toErrorBody(error).asBadRequest()
        is ValidationErrors -> toErrorBody(error).asBadRequest()
        is UnexpectedRefiningError -> toErrorBody(error).asInternalServerError()
        is UnexpectedDataParsingError -> toErrorBody(error).asInternalServerError()
        is DatabaseFieldMappingError -> TODO()
        is UnmappableDatabaseFieldType<*> -> TODO()
    }

private fun toErrorBody(error: ValidationErrors) = ErrorResponse(
    errorType = error.javaClass.simpleName,
    errors = error.messages()
)

private fun toErrorBody(error: ExternalDataParsingError) = ErrorResponse(
    errorType = error.javaClass.simpleName,
    errors = setOf(error.message)
)
