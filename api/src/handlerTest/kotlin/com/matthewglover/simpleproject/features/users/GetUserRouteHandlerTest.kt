package com.matthewglover.simpleproject.features.users

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
        val userService = mockk<UserService>()
        val userIdSlot = slot<String>()
        coEvery { userService.getUser(capture(userIdSlot)) } answers { User(userIdSlot.captured) }

        val webClient = setupWebClient(userService)

        webClient
            .get().uri("/users/1234")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<User>().isEqualTo(User("1234"))
    }

    @Test
    fun `an invalid userId returns not found`() {
        val userService = mockk<UserService>()
        coEvery { userService.getUser(any()) } answers { null }

        val webClient = setupWebClient(userService)

        webClient
            .get().uri("/users/1234")
            .exchange()
            .expectStatus().isNotFound
    }

    private fun setupWebClient(userService: UserService): WebTestClient {
        val routeUtils = MockRouteUtils()
        val userHandlers = UserHandlers(userService)
        val userRoutes = UserRouteConfig().userRoutes(routeUtils, userHandlers)

        return WebTestClient
            .bindToRouterFunction(userRoutes)
            .build()
    }
}