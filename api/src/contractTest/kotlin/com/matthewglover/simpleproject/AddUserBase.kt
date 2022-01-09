package com.matthewglover.simpleproject

import com.matthewglover.simpleproject.features.users.NewUser
import com.matthewglover.simpleproject.features.users.User
import com.matthewglover.simpleproject.features.users.UserHandlers
import com.matthewglover.simpleproject.features.users.UserRepository
import com.matthewglover.simpleproject.features.users.UserRouteConfig
import io.mockk.coEvery
import io.mockk.mockk
import io.restassured.module.webtestclient.RestAssuredWebTestClient
import org.junit.jupiter.api.BeforeEach

open class AddUserBase {

    @BeforeEach
    fun setup() {
        val userRepository = mockk<UserRepository>()
        configureGetUserStubs(userRepository)

        val userHandlers = UserHandlers(userRepository)

        val userRoutes = UserRouteConfig().userRoutes(TestRouteUtils.routeUtils(), userHandlers)

        RestAssuredWebTestClient.standaloneSetup(userRoutes)
    }

    private fun configureGetUserStubs(userRepository: UserRepository) {
        coEvery { userRepository.addUser(NewUser(username = "valid-user-name")) } returns User("new-user-id")
    }
}
