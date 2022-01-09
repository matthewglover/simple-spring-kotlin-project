package com.matthewglover.simpleproject.common.errors

sealed interface ContextError : ApplicationError
object ReactorContextReadError : ContextError
object RequestLoggingContextReadError : ContextError
