package com.matthewglover.simpleproject.common.requestinput

import arrow.core.right
import com.matthewglover.simpleproject.common.errors.ValidationErrors
import com.matthewglover.simpleproject.features.users.NewUser
import com.matthewglover.simpleproject.features.users.RefinedNewUser
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class RequestInputRefinerTest {

    companion object {
        @JvmStatic
        fun provideInvalidUnrefinedObjects(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(NewUser(null))
            )
        }
    }

    @Test
    internal fun `returns Right of refined type for valid unrefined type`() {
        val newUser = NewUser("boom")

        assertEquals(RefinedNewUser("boom").right(), RequestInputRefiner.refine(newUser))
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUnrefinedObjects")
    internal fun <Refined> `returns Left of ValidationErrors for an invalid Unrefined type`(
        unrefined: Refineable<Refined>
    ) {
        val actual = RequestInputRefiner.refine(unrefined)

        assertTrue(actual.isLeft())

        actual.tapLeft {
            assertThat(it, instanceOf(ValidationErrors::class.java))
        }
    }
}
