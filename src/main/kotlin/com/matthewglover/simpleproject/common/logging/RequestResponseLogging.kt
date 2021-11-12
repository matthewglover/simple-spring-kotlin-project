package com.matthewglover.simpleproject.common.logging

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

@Component
class RequestResponseLogging(contextLoggerFactory: ContextLoggerFactory) {

    private val logger = contextLoggerFactory.create(RequestResponseLogging::class.java)

    suspend fun filter(serverRequest: ServerRequest, next: NextHandler): ServerResponse {

        logger.info { Tags("requestStatus" to "Starting") }

        val serverResponse = next(serverRequest)

        logger.info {
            Tags(
                "requestStatus" to "Completed",
                "statusCode" to serverResponse.rawStatusCode().toString(),
                "reasonPhrase" to serverResponse.statusCode().reasonPhrase
            )
        }

        return serverResponse
    }
}
