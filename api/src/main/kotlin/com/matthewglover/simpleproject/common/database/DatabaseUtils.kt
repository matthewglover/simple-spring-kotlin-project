package com.matthewglover.simpleproject.common.database

import arrow.core.Either
import arrow.core.left
import com.matthewglover.simpleproject.errors.DatabaseFieldMappingError
import com.matthewglover.simpleproject.errors.DatabaseQueryError
import com.matthewglover.simpleproject.errors.ExternalDataParsingError
import com.matthewglover.simpleproject.errors.UnmappableDatabaseFieldType
import io.r2dbc.spi.Row
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.r2dbc.core.DatabaseClient

suspend fun DatabaseClient.GenericExecuteSpec.awaitFirstRow(): Either<DatabaseQueryError, Row> =
    Either.catch {
        this
            .map { row -> row }
            .one()
            .awaitFirst()
    }.mapLeft { cause -> DatabaseQueryError(cause) }

inline fun <reified T : Any> Row.parseValue(fieldName: String): Either<ExternalDataParsingError, T?> {
    return when (T::class) {
        Int::class -> catchExceptions(fieldName) { get(fieldName, Integer::class.java)?.toInt() as T }
        String::class -> catchExceptions(fieldName) { get(fieldName, T::class.java) }
        else -> UnmappableDatabaseFieldType(T::class).left()
    }
}

fun <T> catchExceptions(fieldName: String, f: () -> T?): Either<ExternalDataParsingError, T?> =
    Either.catch(f).mapLeft { cause -> DatabaseFieldMappingError(fieldName, cause) }
