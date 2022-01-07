package com.matthewglover.simpleproject.features.users

import arrow.core.computations.either
import com.matthewglover.simpleproject.common.errors.RequestDataParsingError
import com.matthewglover.simpleproject.common.requestinput.RequestDataParsingErrorResponseMapper
import com.matthewglover.simpleproject.common.requestinput.RequestInput
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait

@Component
class UserHandlers(private val userService: UserService) {

    companion object {
        private suspend fun <T : Any> okResponse(t: T) =
            ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValueAndAwait(t)

        private suspend fun userNotFoundResponse() =
            ServerResponse.notFound().buildAndAwait()
    }

    suspend fun handleGetUser(request: ServerRequest): ServerResponse {
        val userId: String = request.pathVariable("userId")

        return userService.getUser(userId)
            ?.let { okResponse(it) } ?: userNotFoundResponse()
    }

    suspend fun handleAddUser(request: ServerRequest): ServerResponse {
        val result = either<RequestDataParsingError, User> {
            val newUser = RequestInput.parseAndValidate<RawNewUser, NewUser>(request).bind()

            userService.addUser(newUser)
        }

        return result
            .fold(
                { error -> RequestDataParsingErrorResponseMapper.map(error) },
                { user -> okResponse(user) }
            )
    }
}
