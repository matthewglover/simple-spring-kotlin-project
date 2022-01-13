package com.matthewglover.simpleproject.common.response

import com.matthewglover.simpleproject.errors.ApplicationError
import com.matthewglover.simpleproject.errors.ContextError
import com.matthewglover.simpleproject.errors.DatabaseError
import com.matthewglover.simpleproject.errors.ExternalDataParsingError
import com.matthewglover.simpleproject.errors.RequestDataError
import com.matthewglover.simpleproject.errors.UserNotFoundError
import com.matthewglover.simpleproject.errors.UserRepositoryError
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.buildAndAwait

suspend fun ApplicationError.asError(): ServerResponse = when (this) {
    is RequestDataError -> mapRequestDataParsingError(this)
    is ContextError -> mapContextError(this)
    is UserRepositoryError -> mapUserRepositoryError(this)
    is ExternalDataParsingError -> mapExternalDataConversion(this)
    is DatabaseError -> TODO()
}

suspend fun mapUserRepositoryError(userRepositoryError: UserRepositoryError): ServerResponse =
    when (userRepositoryError) {
        is UserNotFoundError -> ServerResponse.notFound().buildAndAwait()
    }
