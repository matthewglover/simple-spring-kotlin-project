package com.matthewglover.simpleproject.features.users

import org.springframework.stereotype.Repository

@Repository
class UserRepository(private val users: List<User>) {

    suspend fun findUserById(userId: String): User? =
        users.find { it.userId == userId }
}
