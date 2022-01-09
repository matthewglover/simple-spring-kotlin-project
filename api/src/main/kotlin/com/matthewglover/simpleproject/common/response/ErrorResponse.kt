package com.matthewglover.simpleproject.common.response

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

data class ErrorResponse(val errorType: String, val errors: Set<String>)

suspend fun ErrorResponse.asBadRequest() =
    ServerResponse.badRequest()
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValueAndAwait(this)

suspend fun ErrorResponse.asUnsupportedMedia() =
    ServerResponse.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValueAndAwait(this)

suspend fun ErrorResponse.asInternalServerError() =
    ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValueAndAwait(this)
