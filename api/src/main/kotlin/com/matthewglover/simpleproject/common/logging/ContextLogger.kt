package com.matthewglover.simpleproject.common.logging

import arrow.core.Either
import arrow.core.flatMap
import com.matthewglover.simpleproject.common.context.ReactorContextUtil
import com.matthewglover.simpleproject.common.errors.ContextErrors
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.message.StringMapMessage
import org.apache.logging.log4j.util.MessageSupplier
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

typealias TagProvider = () -> Tags

typealias MessageLogger = (MessageSupplier) -> Unit

typealias ErrorMessageLogger = (MessageSupplier, Throwable) -> Unit

@Component
class ContextLoggerFactory {

    fun <T> create(clazz: Class<T>, currentTime: CurrentTimeProvider = System::nanoTime): ContextLogger<T> =
        ContextLogger(clazz, currentTime)
}

class ContextLogger<T>(
    clazz: Class<T>,
    private val currentTime: CurrentTimeProvider = System::nanoTime
) {

    private val logger = LogManager.getLogger(clazz)

    suspend fun info(providedTags: TagProvider) {
        if (logger.isInfoEnabled) {
            log(logger::info, providedTags)
        }
    }

    suspend fun error(providedTags: TagProvider) {
        if (logger.isErrorEnabled) {
            log(logger::error, providedTags)
        }
    }

    suspend fun error(providedTags: TagProvider, throwable: Throwable) {
        if (logger.isErrorEnabled) {
            log(logger::error, providedTags, throwable)
        }
    }

    private suspend fun log(logMessage: MessageLogger, providedTags: TagProvider) {
        createDefaultTags()
            .map { it + providedTags() }
            .tap { logMessage { StringMapMessage(it) } }
    }

    private suspend fun log(logMessageAndError: ErrorMessageLogger, providedTags: TagProvider, throwable: Throwable) {
        createDefaultTags()
            .map { it + providedTags() }
            .tap { logMessageAndError({ StringMapMessage(it) }, throwable) }
    }

    private suspend fun createDefaultTags(): Either<ContextErrors, Tags> {
        return ReactorContextUtil.readContext()
            .flatMap { it.readRequestLoggingContext() }
            .map {
                it.toTags() + Tags(
                    "elapsedMillis" to it.startTime.elapsedMillis(currentTime()).toString()
                )
            }
    }
}

fun Long.elapsedMillis(currentTime: Long): Long = TimeUnit.NANOSECONDS.toMillis(currentTime - this)
