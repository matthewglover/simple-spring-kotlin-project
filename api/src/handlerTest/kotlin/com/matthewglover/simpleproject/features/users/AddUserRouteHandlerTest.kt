package com.matthewglover.simpleproject.features.users

import com.matthewglover.simpleproject.common.errors.util.ErrorResponse
import com.matthewglover.simpleproject.utils.MockRouteUtils
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.boot.context.properties.bind.validation.ValidationErrors
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

class AddUserRouteHandlerTest {

    @Test
    fun `a valid user payload returns a newly created user`() {
        val userService = mockk<UserService>()
        val newUser = NewUser("test@test.com")
        coEvery { userService.addUser(newUser) } answers { User("new-user-id") }

        val webClient = setupWebClient(userService)

        webClient
            .post().uri("/users")
            .bodyValue(newUser)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<User>().isEqualTo(User("new-user-id"))
    }

    @Test
    fun `a malformed json payload returns a Bad Request`() {
        val userService = mockk<UserService>()

        val webClient = setupWebClient(userService)

        webClient
            .post().uri("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{")
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath(".errorType").isEqualTo("JsonDecodingError")
    }

    @Test
    fun `an invalid user payload returns a Bad Request`() {
        val userService = mockk<UserService>()

        val webClient = setupWebClient(userService)

        webClient
            .post().uri("/users")
            .bodyValue(InvalidPayload("invalidValue"))
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<ErrorResponse>().isEqualTo(
                ErrorResponse(
                    errorType = ValidationErrors::class.java.simpleName,
                    errors = setOf("username required")
                )
            )
    }

    data class InvalidPayload(val property: String)

    private fun setupWebClient(userService: UserService): WebTestClient {
        val routeUtils = MockRouteUtils()
        val userHandlers = UserHandlers(userService)
        val userRoutes = UserRouteConfig().userRoutes(routeUtils, userHandlers)

        return WebTestClient
            .bindToRouterFunction(userRoutes)
            .build()
    }
}
