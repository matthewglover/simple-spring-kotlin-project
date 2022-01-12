package com.matthewglover.simpleproject.errors

sealed interface UserRepositoryError : ApplicationError {
    val message: String
}

data class UserNotFoundError(val userId: String) : UserRepositoryError {
    override val message: String
        get() = "No user found with id \"$userId\""
}
