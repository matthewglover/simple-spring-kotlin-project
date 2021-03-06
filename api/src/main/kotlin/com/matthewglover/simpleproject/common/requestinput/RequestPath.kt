package com.matthewglover.simpleproject.common.requestinput

import arrow.core.Either
import arrow.core.flatMap
import com.matthewglover.simpleproject.errors.InvalidPathVariableNameError
import com.matthewglover.simpleproject.errors.InvalidPathVariableValue
import com.matthewglover.simpleproject.errors.RequestDataError
import org.springframework.web.reactive.function.server.ServerRequest

fun ServerRequest.parsePathVariable(pathVariableName: String): Either<RequestDataError, String> =
    parsePathVariable(pathVariableName) { it }

fun <T> ServerRequest.parsePathVariable(
    pathVariableName: String,
    fromString: (String) -> T
): Either<RequestDataError, T> =
    getPathVariable(pathVariableName)
        .flatMap { it.safelyParse(fromString) }

private fun ServerRequest.getPathVariable(pathVariableName: String) =
    Either.catch { this.pathVariable(pathVariableName) }
        .mapLeft { InvalidPathVariableNameError(it) }

private fun <T> String.safelyParse(fromString: (String) -> T): Either<RequestDataError, T> =
    Either.catch { fromString(this) }
        .mapLeft { InvalidPathVariableValue(it) }
