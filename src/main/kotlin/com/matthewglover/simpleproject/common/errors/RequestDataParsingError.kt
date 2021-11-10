package com.matthewglover.simpleproject.common.errors

import org.springframework.core.codec.DecodingException

sealed interface RequestDataParsingError

data class JsonDecodingError(val decodingException: DecodingException) : RequestDataParsingError

sealed interface RefiningError : RequestDataParsingError
data class ValidationErrors(val errors: Set<ValidationError>) : RefiningError
data class UnexpectedRefiningError(val throwable: Throwable) : RefiningError

data class ValidationError(val message: String)

fun ValidationErrors.messages(): Set<String> = errors.map { it.message }.toSet()
