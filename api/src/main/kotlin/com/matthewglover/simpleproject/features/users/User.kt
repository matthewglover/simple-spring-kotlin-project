package com.matthewglover.simpleproject.features.users

import com.fasterxml.jackson.annotation.JsonValue
import com.matthewglover.simpleproject.common.requestinput.Refineable
import java.util.regex.Pattern
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

private const val EMAIL_REG_EX: String = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@" +
    "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" +
    "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\." +
    "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" +
    "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|" +
    "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"

private const val MINIMUM_AGE: Int = 18

private val EmailPattern: Pattern = Pattern.compile(EMAIL_REG_EX)

data class User(val userId: String, val email: Email, val age: UserAge)

data class RawUser(
    val userId: String?,
    val email: String?,
    val age: Int?
) : Refineable<User> {
    override fun unsafeRefine(): User {
        return User(
            userId = userId!!,
            email = Email(email!!),
            age = UserAge(age!!)
        )
    }
}

data class NewUser(val email: Email, val age: UserAge)

data class RawNewUser(
    @get: NotBlank(message = "valid email required")
    @get: javax.validation.constraints.Email(message = "valid email required", regexp = EMAIL_REG_EX)
    val email: String?,

    @get: Min(value = 18, message = "age must be 18 or over")
    val age: Int
) : Refineable<NewUser> {
    override fun unsafeRefine(): NewUser = NewUser(
        email = Email(email!!),
        age = UserAge(age)
    )
}

@JvmInline
value class UserAge(
    @JsonValue
    val value: Int
) {
    init {
        require(value >= MINIMUM_AGE) { "Age: should be 18 or over" }
    }
}

@JvmInline
value class Email(val value: String) {
    init {
        require(EmailPattern.matcher(value).matches()) { "Email: should be a validly formatted email" }
    }
}
