package com.matthewglover.simpleproject.common.requestinput

import arrow.core.Either
import arrow.core.flatMap
import com.matthewglover.simpleproject.common.errors.RequestDataParsingError

typealias RefinedResponse<Refined> = Either<RequestDataParsingError, Refined>

object RequestInputRefiner {

    fun <Unrefined : Refineable<Refined>, Refined> refine(unrefined: Unrefined): RefinedResponse<Refined> {
        return InputValidator.validate(unrefined)
            .flatMap { it.refine() }
    }
}
