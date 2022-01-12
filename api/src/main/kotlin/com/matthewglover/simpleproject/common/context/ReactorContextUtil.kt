package com.matthewglover.simpleproject.common.context

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.matthewglover.simpleproject.errors.ReactorContextReadError
import kotlinx.coroutines.reactor.ReactorContext
import kotlinx.coroutines.reactor.asCoroutineContext
import reactor.util.context.Context
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

object ReactorContextUtil {

    suspend fun readContext(): Either<ReactorContextReadError, Context> {
        return coroutineContext[ReactorContext]?.context?.right()
            ?: ReactorContextReadError.left()
    }

    suspend inline fun <reified T : Any> writeData(data: T): Either<ReactorContextReadError, CoroutineContext> =
        readContext()
            .map { it.put(T::class, data).asCoroutineContext() }
}
