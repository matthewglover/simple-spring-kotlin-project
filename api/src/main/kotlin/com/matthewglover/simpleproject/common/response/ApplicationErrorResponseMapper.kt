package com.matthewglover.simpleproject.common.response

import com.matthewglover.simpleproject.errors.ApplicationError
import com.matthewglover.simpleproject.errors.ContextError
import com.matthewglover.simpleproject.errors.RequestDataParsingError
import com.matthewglover.simpleproject.errors.UserNotFoundError
import com.matthewglover.simpleproject.errors.UserRepositoryError
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.buildAndAwait

suspend fun ApplicationError.asError(): ServerResponse = when (this) {
    is RequestDataParsingError -> mapRequestDataParsingError(this)
    is ContextError -> mapContextError(this)
    is UserRepositoryError -> mapUserRepositoryError(this)
}

suspend fun mapUserRepositoryError(userRepositoryError: UserRepositoryError): ServerResponse =
    when (userRepositoryError) {
        is UserNotFoundError -> ServerResponse.notFound().buildAndAwait()
    }
