package com.matthewglover.simpleproject.features.users

import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {
    suspend fun getUser(userId: String): User? {
        return userRepository.findUserById(userId)
    }

    suspend fun addUser(newUser: NewUser): User {
        return userRepository.addUser(newUser)
    }
}
