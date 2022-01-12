package com.matthewglover.simpleproject.features.users

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.matthewglover.simpleproject.errors.UserNotFoundError
import com.matthewglover.simpleproject.errors.UserRepositoryError
import org.springframework.stereotype.Repository

@Repository
class UserRepository(private val users: MutableList<User>) {

    suspend fun findUserById(userId: String): Either<UserRepositoryError, User> =
        users.find { it.userId == userId }?.right() ?: UserNotFoundError(userId).left()

    @Suppress("UnusedPrivateMember")
    suspend fun addUser(newUser: NewUser): User {
        val user = User("new-user-id")
        users.add(user)

        return user
    }
}
