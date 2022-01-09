package com.matthewglover.simpleproject.features.users

import arrow.core.computations.either
import com.matthewglover.simpleproject.common.errors.RequestDataParsingError
import com.matthewglover.simpleproject.common.requestinput.RequestInput
import com.matthewglover.simpleproject.common.response.asOk
import com.matthewglover.simpleproject.common.response.userNotFound
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

@Component
class UserHandlers(private val userRepository: UserRepository) {

    suspend fun handleGetUser(request: ServerRequest): ServerResponse {
        val userId: String = request.pathVariable("userId")

        return userRepository.findUserById(userId)?.asOk() ?: userNotFound()
    }

    suspend fun handleAddUser(request: ServerRequest): ServerResponse = either<RequestDataParsingError, User> {
        val newUser = RequestInput.parseAndValidate<RawNewUser, NewUser>(request).bind()

        userRepository.addUser(newUser)
    }.asOk()
}
