package com.matthewglover.simpleproject.common.response

import com.matthewglover.simpleproject.common.errors.ApplicationError
import com.matthewglover.simpleproject.common.errors.RequestDataParsingError
import org.springframework.web.reactive.function.server.ServerResponse

suspend fun ApplicationError.asError(): ServerResponse = when (this) {
    is RequestDataParsingError -> RequestDataParsingErrorResponseMapper.map(this)
    else -> TODO("Implement me")
}
