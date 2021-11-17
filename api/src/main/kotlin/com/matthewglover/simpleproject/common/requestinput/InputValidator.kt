package com.matthewglover.simpleproject.common.requestinput

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.matthewglover.simpleproject.common.errors.ValidationError
import com.matthewglover.simpleproject.common.errors.ValidationErrors
import javax.validation.ConstraintViolation
import javax.validation.Validation

object InputValidator {
    val validator = Validation.buildDefaultValidatorFactory().validator

    fun <T> validate(t: T): Either<ValidationErrors, T> {
        val constraintViolations = validator.validate(t)

        return if (constraintViolations.isNotEmpty()) {
            toValidationErrors(constraintViolations).left()
        } else {
            t.right()
        }
    }

    private fun <Unrefined> toValidationErrors(
        constraintViolations: Set<ConstraintViolation<Unrefined>>
    ): ValidationErrors {
        val errors = constraintViolations.map { toValidationError(it) }.toSet()

        return ValidationErrors(errors)
    }

    private fun <Unrefined> toValidationError(constraintViolation: ConstraintViolation<Unrefined>): ValidationError {
        return ValidationError(constraintViolation.message)
    }
}
