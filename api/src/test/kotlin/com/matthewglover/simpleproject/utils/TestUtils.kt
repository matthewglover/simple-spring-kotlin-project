package com.matthewglover.simpleproject.utils

import arrow.core.Either

@Suppress("TooGenericExceptionThrown")
fun <L, R> Either<L, R>.getOrThrow(): R =
    this.fold({ throw RuntimeException("Unexpected error") }, { it })
