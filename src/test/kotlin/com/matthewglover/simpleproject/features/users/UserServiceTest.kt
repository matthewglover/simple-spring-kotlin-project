package com.matthewglover.simpleproject.features.users

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class UserServiceTest {

    @Test
    fun `returns user when valid id`() = runBlocking {
        val userRepository = mockk<UserRepository>()
        coEvery { userRepository.findUserById("valid-user-id") } returns User("valid-user-id")

        val userService = UserService(userRepository)

        assertEquals(User("valid-user-id"), userService.findByUserId("valid-user-id"))
    }

    @Test
    fun `returns null when no user with id`() = runBlocking {
        val userRepository = mockk<UserRepository>()
        coEvery { userRepository.findUserById("invalid-user-id") } returns null

        val userService = UserService(userRepository)

        assertNull(userService.findByUserId("invalid-user-id"))
    }
}
