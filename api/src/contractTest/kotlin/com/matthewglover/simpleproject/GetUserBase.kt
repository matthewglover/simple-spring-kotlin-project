package com.matthewglover.simpleproject

import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.matthewglover.simpleproject.errors.UserNotFoundError
import com.matthewglover.simpleproject.features.users.RawUser
import com.matthewglover.simpleproject.features.users.User
import com.matthewglover.simpleproject.features.users.UserHandlers
import com.matthewglover.simpleproject.features.users.UserRepository
import com.matthewglover.simpleproject.features.users.UserRouteConfig
import io.mockk.coEvery
import io.mockk.mockk
import io.restassured.module.webtestclient.RestAssuredWebTestClient
import org.junit.jupiter.api.BeforeEach
import org.springframework.util.ResourceUtils

open class GetUserBase {

    companion object {
        private val validUser = loadValidUser()

        private fun loadValidUser(): User {
            val file = ResourceUtils.getFile("classpath:contracts/getUser/valid_user.json")

            return jacksonObjectMapper().readValue<RawUser>(file).unsafeRefine()
        }
    }

    @BeforeEach
    fun setup() {
        val userRepository = mockk<UserRepository>()
        configureGetUserStubs(userRepository)

        val userHandlers = UserHandlers(userRepository)
        val userRoutes = UserRouteConfig().userRoutes(TestRouteUtils.routeUtils(), userHandlers)

        RestAssuredWebTestClient.standaloneSetup(userRoutes)
    }

    private fun configureGetUserStubs(userRepository: UserRepository) {
        coEvery { userRepository.findUserById("valid-user-id") } returns validUser.right()
        coEvery { userRepository.findUserById("invalid-user-id") } returns UserNotFoundError("invalid-user-id").left()
    }
}
