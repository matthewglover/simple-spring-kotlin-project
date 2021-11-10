package com.matthewglover.simpleproject.common.requestinput

import arrow.core.left
import arrow.core.right
import com.matthewglover.simpleproject.common.errors.ValidationError
import com.matthewglover.simpleproject.common.errors.ValidationErrors
import com.matthewglover.simpleproject.features.users.NewUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class InputValidatorTest {

    @Test
    fun `valid data returns right of input data`() {
        val newUser = NewUser("blah")

        val actual = InputValidator.validate(newUser)

        assertEquals(newUser.right(), actual)
    }

    @Test
    fun `invalid data returns left of errors for invalid NewUser`() {
        val newUser = NewUser(null)

        val actual = InputValidator.validate(newUser)

        val expected = ValidationErrors(setOf(ValidationError("username required"))).left()

        assertEquals(expected, actual)
    }
}
