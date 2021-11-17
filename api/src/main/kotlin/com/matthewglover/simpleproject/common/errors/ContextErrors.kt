package com.matthewglover.simpleproject.common.errors

sealed interface ContextErrors : ApplicationErrors
object ReactorContextReadError : ContextErrors
object RequestLoggingContextReadError : ContextErrors
