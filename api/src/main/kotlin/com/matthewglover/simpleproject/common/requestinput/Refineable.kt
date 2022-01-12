package com.matthewglover.simpleproject.common.requestinput

import arrow.core.Either
import com.matthewglover.simpleproject.errors.UnexpectedRefiningError

interface Refineable<RefinedType> {
    fun unsafeRefine(): RefinedType

    fun refine(): Either<UnexpectedRefiningError, RefinedType> =
        Either
            .catch { unsafeRefine() }
            .mapLeft { UnexpectedRefiningError(it) }
}
