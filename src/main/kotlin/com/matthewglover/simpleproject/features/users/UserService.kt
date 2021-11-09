package com.matthewglover.simpleproject.features.users

import org.springframework.stereotype.Service

@Service
class UserService {
    suspend fun findByUserId(userId: String): User {
        return User(userId)
    }
}
