package com.matthewglover.simpleproject.features.users

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UserRepositoryConfig {

    @Bean
    fun userList(): List<User> = listOf(
        User("1234"),
        User("abcd")
    )
}
