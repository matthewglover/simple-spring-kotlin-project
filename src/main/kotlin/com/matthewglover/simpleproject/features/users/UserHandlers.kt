package com.matthewglover.simpleproject.features.users

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class UserHandlers(private val userService: UserService) {

    suspend fun handleGetUser(request: ServerRequest): ServerResponse {
        val userId: String = request.pathVariable("userId")
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(userService.findByUserId(userId))
    }
}
