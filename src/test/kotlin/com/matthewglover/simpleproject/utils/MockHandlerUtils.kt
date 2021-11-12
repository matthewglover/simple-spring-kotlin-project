package com.matthewglover.simpleproject.utils

import com.matthewglover.simpleproject.common.logging.NextHandler
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.buildAndAwait

object MockHandlerUtils {

    @Suppress("UnusedPrivateMember")
    suspend fun mockNext(httpStatus: HttpStatus): NextHandler = { _: ServerRequest ->
        ServerResponse
            .status(httpStatus)
            .buildAndAwait()
    }
}
