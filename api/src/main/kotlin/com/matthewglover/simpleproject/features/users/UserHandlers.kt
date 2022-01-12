package com.matthewglover.simpleproject.features.users

import arrow.core.computations.either
import com.matthewglover.simpleproject.common.errors.ApplicationError
import com.matthewglover.simpleproject.common.errors.RequestDataParsingError
import com.matthewglover.simpleproject.common.requestinput.parseBody
import com.matthewglover.simpleproject.common.requestinput.parsePathVariable
import com.matthewglover.simpleproject.common.response.asOk
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

@Component
class UserHandlers(private val userRepository: UserRepository) {

    suspend fun handleGetUser(request: ServerRequest): ServerResponse = either<ApplicationError, User> {
        val userId: String = request.parsePathVariable("userId").bind()

        userRepository.findUserById(userId).bind()
    }.asOk()

    suspend fun handleAddUser(request: ServerRequest): ServerResponse = either<RequestDataParsingError, User> {
        val newUser = request.parseBody<RawNewUser, NewUser>().bind()

        userRepository.addUser(newUser)
    }.asOk()
}
