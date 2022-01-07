package com.matthewglover.simpleproject.features.users

import org.springframework.stereotype.Repository

@Repository
class UserRepository(private val users: MutableList<User>) {

    suspend fun findUserById(userId: String): User? =
        users.find { it.userId == userId }

    @Suppress("UnusedPrivateMember")
    suspend fun addUser(newUser: NewUser): User {
        val user = User("new-user-id")
        users.add(user)

        return user
    }
}
