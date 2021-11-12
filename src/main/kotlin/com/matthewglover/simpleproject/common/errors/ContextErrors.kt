package com.matthewglover.simpleproject.common.errors

sealed interface ContextErrors
object ReactorContextReadError : ContextErrors
object RequestLoggingContextReadError : ContextErrors
