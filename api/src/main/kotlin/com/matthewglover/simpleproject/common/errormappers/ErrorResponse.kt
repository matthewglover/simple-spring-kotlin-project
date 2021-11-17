package com.matthewglover.simpleproject.common.errormappers

data class ErrorResponse(val errorType: String, val errors: Set<String>)
