package com.matthewglover.simpleproject

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class GreetHandler {

    suspend fun greet(request: ServerRequest): ServerResponse =
        ServerResponse.ok()
            .contentType(MediaType.TEXT_PLAIN)
            .bodyValueAndAwait("Hello, world")
}
