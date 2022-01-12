package com.matthewglover.simpleproject.features.users

import arrow.core.left
import arrow.core.right
import com.matthewglover.simpleproject.errors.UserNotFoundError
import com.matthewglover.simpleproject.utils.MockRouteUtils
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

class GetUserRouteHandlerTest {

    @Test
    fun `a valid userId returns a valid user`() {
        val userRepository = mockk<UserRepository>()
        val userIdSlot = slot<String>()
        coEvery { userRepository.findUserById(capture(userIdSlot)) } answers { User(userIdSlot.captured).right() }

        val webClient = setupWebClient(userRepository)

        webClient
            .get().uri("/users/1234")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<User>().isEqualTo(User("1234"))
    }

    @Test
    fun `an invalid userId returns not found`() {
        val userRepository = mockk<UserRepository>()
        val userIdSlot = slot<String>()
        coEvery { userRepository.findUserById(capture(userIdSlot)) } answers {
            UserNotFoundError(userIdSlot.captured).left()
        }

        val webClient = setupWebClient(userRepository)

        webClient
            .get().uri("/users/1234")
            .exchange()
            .expectStatus().isNotFound
    }

    private fun setupWebClient(userRepository: UserRepository): WebTestClient {
        val routeUtils = MockRouteUtils()
        val userHandlers = UserHandlers(userRepository)
        val userRoutes = UserRouteConfig().userRoutes(routeUtils, userHandlers)

        return WebTestClient
            .bindToRouterFunction(userRoutes)
            .build()
    }
}
