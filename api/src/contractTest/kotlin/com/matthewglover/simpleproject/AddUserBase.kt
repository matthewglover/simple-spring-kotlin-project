package com.matthewglover.simpleproject

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.matthewglover.simpleproject.features.users.NewUser
import com.matthewglover.simpleproject.features.users.RawNewUser
import com.matthewglover.simpleproject.features.users.User
import com.matthewglover.simpleproject.features.users.UserHandlers
import com.matthewglover.simpleproject.features.users.UserRepository
import com.matthewglover.simpleproject.features.users.UserRouteConfig
import io.mockk.coEvery
import io.mockk.mockk
import io.restassured.module.webtestclient.RestAssuredWebTestClient
import org.junit.jupiter.api.BeforeEach
import org.springframework.util.ResourceUtils

open class AddUserBase {

    companion object {
        private const val newUserId = "new-user-id"
        private val validUser = loadValidUser()

        private fun loadValidUser(): NewUser {
            val file = ResourceUtils.getFile("classpath:contracts/addUser/valid_new_user_payload.json")
            val objectMapper = jacksonObjectMapper()

            return objectMapper.readValue<RawNewUser>(file).unsafeRefine()
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
        coEvery { userRepository.addUser(validUser) } returns User(newUserId)
    }
}
