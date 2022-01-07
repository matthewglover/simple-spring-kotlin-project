package com.matthewglover.simpleproject.common.requestinput

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import com.matthewglover.simpleproject.common.errors.JsonDecodingError
import com.matthewglover.simpleproject.common.errors.MissingRequestPayloadError
import com.matthewglover.simpleproject.common.errors.RequestDataParsingError
import com.matthewglover.simpleproject.common.errors.UnexpectedRequestDataParsingError
import com.matthewglover.simpleproject.common.errors.UnsupportedMediaTypeError
import com.matthewglover.simpleproject.common.errors.ValidationError
import com.matthewglover.simpleproject.common.errors.ValidationErrors
import org.springframework.core.codec.DecodingException
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.server.ServerWebInputException
import org.springframework.web.server.UnsupportedMediaTypeStatusException
import javax.validation.ConstraintViolation
import javax.validation.Validation
import javax.validation.Validator

object RequestInput {

    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    suspend inline fun <reified RawType : Refineable<RefinedType>, reified RefinedType> parseAndValidate(
        request: ServerRequest
    ) = either<RequestDataParsingError, RefinedType> {
        val rawValue = parseBody<RawType>(request).bind()

        val validated = validate(rawValue).bind()

        validated.refine().bind()
    }

    suspend inline fun <reified RawType : Any> parseBody(
        request: ServerRequest
    ): Either<RequestDataParsingError, RawType> =
        Either.catch { request.awaitBody<RawType>() }
            .mapLeft(::handleThrowable)

    fun handleThrowable(throwable: Throwable): RequestDataParsingError = when (throwable) {
        is ServerWebInputException -> jsonDecodingException(throwable)
        is NoSuchElementException -> MissingRequestPayloadError
        is UnsupportedMediaTypeStatusException -> UnsupportedMediaTypeError(throwable)
        else -> UnexpectedRequestDataParsingError(throwable)
    }

    private fun jsonDecodingException(exception: ServerWebInputException): RequestDataParsingError =
        when (val cause = exception.cause) {
            is DecodingException -> JsonDecodingError(cause)
            else -> UnexpectedRequestDataParsingError(exception)
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
}
