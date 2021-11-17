package com.matthewglover.simpleproject

import com.matthewglover.simpleproject.features.users.RefinedNewUser
import com.matthewglover.simpleproject.features.users.User
import com.matthewglover.simpleproject.features.users.UserHandlers
import com.matthewglover.simpleproject.features.users.UserRouteConfig
import com.matthewglover.simpleproject.features.users.UserService
import io.mockk.coEvery
import io.mockk.mockk
import io.restassured.module.webtestclient.RestAssuredWebTestClient
import org.junit.jupiter.api.BeforeEach

open class AddUserBase {

    @BeforeEach
    fun setup() {
        val userService = mockk<UserService>()
        configureGetUserStubs(userService)

        val userHandlers = UserHandlers(userService)

        val userRoutes = UserRouteConfig().userRoutes(TestRouteUtils.routeUtils(), userHandlers)

        RestAssuredWebTestClient.standaloneSetup(userRoutes)
    }

    private fun configureGetUserStubs(userService: UserService) {
        coEvery { userService.addUser(RefinedNewUser(username = "valid-user-name")) } returns User("new-user-id")
    }
}
