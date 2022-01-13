package com.matthewglover.simpleproject.errors

import org.springframework.core.codec.DecodingException
import kotlin.reflect.KClass

sealed interface ExternalDataParsingError : ApplicationError

data class JsonDecodingError(val decodingException: DecodingException) : ExternalDataParsingError {
    override val message: String
        get() = decodingException.message ?: "Decoding error"
}

data class InvalidPathVariableValue(val cause: Throwable) : RequestDataError {
    override val message: String
        get() = "${cause.javaClass.simpleName}: ${cause.message ?: "no error message"}"
}

data class UnexpectedDataParsingError(val cause: Throwable) : ExternalDataParsingError {
    override val message: String
        get() = cause.message ?: "${cause.javaClass.simpleName}: no error message"
}

data class ValidationErrors(val errors: Set<ValidationError>) : ExternalDataParsingError {
    override val message: String
        get() = "Validation errors: ${messages().joinToString(", ")}"
}

fun ValidationErrors.messages(): Set<String> = errors.map { it.message }.toSet()

data class ValidationError(val message: String)

data class UnexpectedRefiningError(val cause: Throwable) : ExternalDataParsingError {
    override val message: String
        get() = cause.message ?: "${cause.javaClass.simpleName}: no error message"
}

data class DatabaseFieldMappingError(val fieldName: String, val cause: Throwable) : ExternalDataParsingError {
    override val message: String
        get() = cause.message ?: "${cause.javaClass.simpleName}: no error message"
}

data class UnmappableDatabaseFieldType<T : Any>(val type: KClass<T>) : ExternalDataParsingError {
    override val message: String
        get() = "Unmappable type: \"${type.simpleName}\""
}
