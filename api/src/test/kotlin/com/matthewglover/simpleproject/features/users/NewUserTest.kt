package com.matthewglover.simpleproject.features.users

import com.matthewglover.simpleproject.errors.UnexpectedRefiningError
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class NewUserTest {

    @Test
    internal fun `refine returns RefinedNewUser for valid unrefined NewUser`() {
        val rawNewUser = RawNewUser(email = "matt@home.com", age = 18)

        val actual = rawNewUser.refine()

        assertTrue(actual.isRight())

        actual.tap { assertEquals(NewUser(email = Email("matt@home.com"), age = UserAge(18)), it) }
    }

    @Test
    internal fun `refine returns Left of ValidationError for invalid unrefined NewUser`() {
        val rawNewUser = RawNewUser(
            email = "invalid email",
            age = 17
        )

        val actual = rawNewUser.refine()

        assertTrue(actual.isLeft())

        actual.tapLeft {
            assertThat(it, instanceOf(UnexpectedRefiningError::class.java))
        }
    }
}
