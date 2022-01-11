package com.matthewglover.simpleproject.common.errors

sealed interface ContextError : ApplicationError {
    val message: String
}
object ReactorContextReadError : ContextError {
    override val message: String = "Unexpectedly failed to read Reactor Context"
}

object RequestLoggingContextReadError : ContextError {
    override val message: String = "Unexpectedly failed to read Request Logging Context"
}
