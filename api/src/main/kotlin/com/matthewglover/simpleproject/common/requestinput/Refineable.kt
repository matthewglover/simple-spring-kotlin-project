package com.matthewglover.simpleproject.common.requestinput

import arrow.core.Either
import com.matthewglover.simpleproject.common.errors.UnexpectedRefiningError

interface Refineable<Refined> {
    fun refine(): Either<UnexpectedRefiningError, Refined>
}
