package com.matthewglover.simpleproject.common.requestinput

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.matthewglover.simpleproject.common.errors.JsonDecodingError
import com.matthewglover.simpleproject.common.errors.RequestDataParsingError
import org.springframework.core.codec.DecodingException
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.server.ServerWebInputException

@Component
object RequestInputParser {

    suspend inline fun <reified T : Any> parseBody(request: ServerRequest): Either<RequestDataParsingError, T> {
        @Suppress("TooGenericExceptionCaught")
        return try {
            request.awaitBody<T>().right()
        } catch (exception: ServerWebInputException) {
            val cause = exception.cause

            if (cause is DecodingException) {
                JsonDecodingError(cause).left()
            } else {
                throw exception
            }
        }
    }
}
