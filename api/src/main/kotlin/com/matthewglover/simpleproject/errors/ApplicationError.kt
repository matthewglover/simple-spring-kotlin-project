package com.matthewglover.simpleproject.errors

sealed interface ApplicationError {
    val message: String
}
