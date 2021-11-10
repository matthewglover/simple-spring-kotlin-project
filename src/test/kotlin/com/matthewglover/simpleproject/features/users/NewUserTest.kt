package com.matthewglover.simpleproject.features.users

import com.matthewglover.simpleproject.common.errors.UnexpectedRefiningError
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class NewUserTest {

    @Test
    internal fun `refine returns Right of RefinedNewUser for valid unrefined NewUser`() {
        val newUser = NewUser("blah")

        val actual = newUser.refine()

        assertTrue(actual.isRight())

        actual.tap { assertEquals(RefinedNewUser("blah"), it) }
    }

    @Test
    internal fun `refine returns Left of ValidationError for invalid unrefined NewUser`() {
        val newUser = NewUser(null)

        val actual = newUser.refine()

        assertTrue(actual.isLeft())

        actual.tapLeft {
            assertThat(it, instanceOf(UnexpectedRefiningError::class.java))
        }
    }
}
