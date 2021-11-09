package com.matthewglover.simpleproject.features.users

import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {
    suspend fun findByUserId(userId: String): User? {
        return userRepository.findUserById(userId)
    }
}
