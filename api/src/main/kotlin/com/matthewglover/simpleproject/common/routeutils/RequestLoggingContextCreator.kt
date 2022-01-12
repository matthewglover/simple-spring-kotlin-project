package com.matthewglover.simpleproject.common.logging

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import com.matthewglover.simpleproject.common.context.ReactorContextUtil
import com.matthewglover.simpleproject.errors.RequestLoggingContextReadError
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.util.context.Context
import java.util.UUID

typealias NextHandler = suspend (ServerRequest) -> ServerResponse
typealias CurrentTimeProvider = () -> Long

@Component
class RequestLoggingContextCreator(val currentTime: CurrentTimeProvider = System::nanoTime) {

    suspend fun writeRequestLoggingContext(request: ServerRequest, next: NextHandler): ServerResponse {
        val requestLoggingContext = toRequestLoggingContext(request)

        return ReactorContextUtil.writeData(requestLoggingContext)
            .map { withContext(it) { next(request) } }
            .getOrElse { throw RequestLoggingContextCreationError() }
    }

    private fun toRequestLoggingContext(request: ServerRequest): RequestLoggingContext =
        RequestLoggingContext(
            correlationId = UUID.randomUUID().toString(),
            startTime = currentTime(),
            method = request.method(),
            requestPath = request.requestPath()
        )
}

fun Context.readRequestLoggingContext(): Either<RequestLoggingContextReadError, RequestLoggingContext> {
    return getOrEmpty<RequestLoggingContext>(RequestLoggingContext::class).orElse(null)
        ?.right()
        ?: RequestLoggingContextReadError.left()
}

class RequestLoggingContextCreationError : RuntimeException("Fatal Error: failed to read Reactor Context")
