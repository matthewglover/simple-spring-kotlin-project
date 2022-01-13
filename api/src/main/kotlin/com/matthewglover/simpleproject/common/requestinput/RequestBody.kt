package com.matthewglover.simpleproject.common.requestinput

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import com.matthewglover.simpleproject.errors.ApplicationError
import com.matthewglover.simpleproject.errors.ExternalDataParsingError
import com.matthewglover.simpleproject.errors.JsonDecodingError
import com.matthewglover.simpleproject.errors.MissingRequestPayloadError
import com.matthewglover.simpleproject.errors.UnexpectedDataParsingError
import com.matthewglover.simpleproject.errors.UnexpectedRequestDataError
import com.matthewglover.simpleproject.errors.UnsupportedMediaTypeError
import com.matthewglover.simpleproject.errors.ValidationError
import com.matthewglover.simpleproject.errors.ValidationErrors
import org.springframework.core.codec.DecodingException
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.server.ServerWebInputException
import org.springframework.web.server.UnsupportedMediaTypeStatusException
import javax.validation.ConstraintViolation
import javax.validation.Validation
import javax.validation.Validator

private val validator: Validator = Validation.buildDefaultValidatorFactory().validator

suspend inline fun <reified RawType : Refineable<RefinedType>, reified RefinedType> ServerRequest.parseBody() =
    either<ApplicationError, RefinedType> {
        val rawValue = deserializeBody<RawType>(this@parseBody).bind()

        val validated = validate(rawValue).bind()

        validated.refine().bind()
    }

suspend inline fun <reified RawType : Any> deserializeBody(
    request: ServerRequest
): Either<ApplicationError, RawType> =
    Either.catch { request.awaitBody<RawType>() }
        .mapLeft(::handleThrowable)

fun handleThrowable(throwable: Throwable): ApplicationError = when (throwable) {
    is ServerWebInputException -> jsonDecodingException(throwable)
    is NoSuchElementException -> MissingRequestPayloadError
    is UnsupportedMediaTypeStatusException -> UnsupportedMediaTypeError(throwable)
    else -> UnexpectedRequestDataError(throwable)
}

private fun jsonDecodingException(exception: ServerWebInputException): ExternalDataParsingError =
    when (val cause = exception.cause) {
        is DecodingException -> JsonDecodingError(cause)
        else -> UnexpectedDataParsingError(exception)
    }

fun <T> validate(t: T): Either<ValidationErrors, T> {
    val constraintViolations = validator.validate(t)

    return if (constraintViolations.isNotEmpty()) {
        toValidationErrors(constraintViolations).left()
    } else {
        t.right()
    }
}

private fun <T> toValidationErrors(
    constraintViolations: Set<ConstraintViolation<T>>
): ValidationErrors {
    val errors = constraintViolations
        .map { constraintViolation -> ValidationError(constraintViolation.message) }
        .toSet()

    return ValidationErrors(errors)
}
