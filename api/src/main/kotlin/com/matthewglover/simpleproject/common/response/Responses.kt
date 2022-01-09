package com.matthewglover.simpleproject.common.response

import arrow.core.Either
import com.matthewglover.simpleproject.common.errors.ApplicationError
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait

suspend fun <T : Any> Either<ApplicationError, T>.asOk(): ServerResponse =
    this.fold(
        { error -> error.asError() },
        { result -> result.asOk() }
    )

suspend fun <T : Any> T.asOk() =
    ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValueAndAwait(this)

suspend fun userNotFound() =
    ServerResponse.notFound().buildAndAwait()
