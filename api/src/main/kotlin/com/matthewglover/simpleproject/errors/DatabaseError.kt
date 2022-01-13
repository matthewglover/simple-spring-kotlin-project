package com.matthewglover.simpleproject.errors

sealed interface DatabaseError : ApplicationError

data class DatabaseQueryError(val cause: Throwable) : DatabaseError {
    override val message: String
        get() = cause.message ?: "${cause.javaClass.simpleName}: no message"
}
