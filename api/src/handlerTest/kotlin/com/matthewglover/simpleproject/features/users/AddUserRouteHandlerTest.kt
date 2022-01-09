package com.matthewglover.simpleproject.features.users

import com.matthewglover.simpleproject.common.response.ErrorResponse
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
        val userRepository = mockk<UserRepository>()
        val newUser = NewUser("test@test.com")
        coEvery { userRepository.addUser(newUser) } answers { User("new-user-id") }

        val webClient = setupWebClient(userRepository)

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
        val userRepository = mockk<UserRepository>()

        val webClient = setupWebClient(userRepository)

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
        val userRepository = mockk<UserRepository>()

        val webClient = setupWebClient(userRepository)

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

    private fun setupWebClient(userRepository: UserRepository): WebTestClient {
        val routeUtils = MockRouteUtils()
        val userHandlers = UserHandlers(userRepository)
        val userRoutes = UserRouteConfig().userRoutes(routeUtils, userHandlers)

        return WebTestClient
            .bindToRouterFunction(userRoutes)
            .build()
    }
}
