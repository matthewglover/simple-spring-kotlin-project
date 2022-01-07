package com.matthewglover.simpleproject.common.errors

import org.springframework.core.codec.DecodingException
import org.springframework.web.server.UnsupportedMediaTypeStatusException

sealed interface RequestDataParsingError : ApplicationErrors {
    val message: String
}

object MissingRequestPayloadError : RequestDataParsingError {
    override val message: String = "No payload in request"
}

data class JsonDecodingError(val decodingException: DecodingException) : RequestDataParsingError {
    override val message: String = decodingException.message ?: "Decoding error"
}

data class UnsupportedMediaTypeError(val cause: UnsupportedMediaTypeStatusException) : RequestDataParsingError {
    override val message: String = "Content type: `${cause.contentType ?: ""}` not supported."
}

data class UnexpectedRequestDataParsingError(val cause: Throwable) : RequestDataParsingError {
    override val message: String = cause.message ?: "${cause.javaClass.simpleName}: no error message"
}

sealed interface RefiningError : RequestDataParsingError
data class ValidationErrors(val errors: Set<ValidationError>) : RefiningError {
    override val message: String = "Validation errors: ${messages().joinToString(", ")}"
}
fun ValidationErrors.messages(): Set<String> = errors.map { it.message }.toSet()
data class ValidationError(val message: String)

data class UnexpectedRefiningError(val cause: Throwable) : RefiningError {
    override val message: String = cause.message ?: "${cause.javaClass.simpleName}: no error message"
}
