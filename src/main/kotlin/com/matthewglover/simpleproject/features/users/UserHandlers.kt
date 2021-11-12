package com.matthewglover.simpleproject.features.users

import arrow.core.computations.either
import com.matthewglover.simpleproject.common.errormappers.RequestDataParsingErrorResponseMapper
import com.matthewglover.simpleproject.common.errors.RequestDataParsingError
import com.matthewglover.simpleproject.common.requestinput.RequestInputParser
import com.matthewglover.simpleproject.common.requestinput.RequestInputRefiner
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
        val refinedNewUser = either<RequestDataParsingError, RefinedNewUser> {
            val newUser = RequestInputParser.parseBody<NewUser>(request).bind()

            RequestInputRefiner.refine(newUser).bind()
        }

        return refinedNewUser.fold(
            { RequestDataParsingErrorResponseMapper.map(it) },
            { okResponse(userService.addUser(it)) }
        )
    }
}
