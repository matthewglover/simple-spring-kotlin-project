package com.matthewglover.simpleproject.features.users

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UserRepositoryConfig {

    @Bean
    fun userList(): MutableList<User> = mutableListOf(
        User("1234"),
        User("abcd")
    )
}
