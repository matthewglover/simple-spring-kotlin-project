package com.matthewglover.simpleproject.errors

sealed interface ContextError : ApplicationError

object ReactorContextReadError : ContextError {
    override val message: String = "Unexpectedly failed to read Reactor Context"
}

object RequestLoggingContextReadError : ContextError {
    override val message: String = "Unexpectedly failed to read Request Logging Context"
}
