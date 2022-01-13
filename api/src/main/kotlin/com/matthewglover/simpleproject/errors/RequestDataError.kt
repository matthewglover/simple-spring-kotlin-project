package com.matthewglover.simpleproject.errors

import org.springframework.web.server.UnsupportedMediaTypeStatusException

sealed interface RequestDataError : ApplicationError

object MissingRequestPayloadError : RequestDataError {
    override val message: String = "No payload in request"
}

data class UnsupportedMediaTypeError(val cause: UnsupportedMediaTypeStatusException) : RequestDataError {
    override val message: String
        get() = "Content type: `${cause.contentType ?: ""}` not supported."
}

data class InvalidPathVariableNameError(val cause: Throwable) : RequestDataError {
    override val message: String
        get() = cause.message ?: "${cause.javaClass.simpleName}: no error message"
}

data class UnexpectedRequestDataError(val cause: Throwable) : RequestDataError {
    override val message: String
        get() = cause.message ?: "${cause.javaClass.simpleName}: no error message"
}
