package com.matthewglover.simpleproject.features.users

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.matthewglover.simpleproject.common.errors.UnexpectedRefiningError
import com.matthewglover.simpleproject.common.requestinput.Refineable
import javax.validation.constraints.NotBlank

data class NewUser(
    @get: NotBlank(message = "username required")
    val username: String?
) : Refineable<RefinedNewUser> {

    override fun refine(): Either<UnexpectedRefiningError, RefinedNewUser> {
        @Suppress("TooGenericExceptionCaught")
        return try {
            RefinedNewUser(username = username!!).right()
        } catch (nullPointerException: NullPointerException) {
            UnexpectedRefiningError(nullPointerException).left()
        }
    }
}
