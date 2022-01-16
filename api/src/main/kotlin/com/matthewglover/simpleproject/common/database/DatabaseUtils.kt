package com.matthewglover.simpleproject.common.database

import arrow.core.Either
import com.matthewglover.simpleproject.errors.DatabaseFieldMappingError
import com.matthewglover.simpleproject.errors.DatabaseQueryError
import com.matthewglover.simpleproject.errors.ExternalDataParsingError
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitOne

suspend fun DatabaseClient.GenericExecuteSpec.awaitFirstRow(): Either<DatabaseQueryError, Map<String, Any>> =
    Either.catch { this.fetch().awaitOne() }
        .mapLeft { cause -> DatabaseQueryError(cause) }

inline fun <reified T : Any> Map<String, Any>.retrieve(fieldName: String): Either<ExternalDataParsingError, T?> {
    return catchExceptions(fieldName) { this[fieldName] as T }
}

fun <T> catchExceptions(fieldName: String, f: () -> T?): Either<ExternalDataParsingError, T?> =
    Either.catch(f).mapLeft { cause -> DatabaseFieldMappingError(fieldName, cause) }
