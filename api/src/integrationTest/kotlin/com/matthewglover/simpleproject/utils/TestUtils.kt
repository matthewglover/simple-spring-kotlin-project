package com.matthewglover.simpleproject.utils

import arrow.core.Either
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.core.io.Resource
import org.springframework.r2dbc.connection.init.ScriptUtils
import org.springframework.r2dbc.core.DatabaseClient
import reactor.kotlin.core.publisher.toMono

typealias SimpleRow = Map<String, Any>

class SimpleRows(private val rawRows: List<SimpleRow>) {
    fun findRowBy(fieldName: String, value: Any): SimpleRow? = rawRows.find { row -> value == row[fieldName] }
}

suspend fun DatabaseClient.executeSqlScript(script: Resource) {
    val connection = connectionFactory.create().toMono().awaitSingle()

    ScriptUtils.executeSqlScript(connection, script).toMono().awaitFirstOrNull()
}

@Suppress("TooGenericExceptionThrown")
fun <L, R> Either<L, R>.getOrThrow(): R =
    this.fold({ throw RuntimeException("Unexpected error") }, { it })

suspend fun DatabaseClient.GenericExecuteSpec.fetchAllRows(): SimpleRows {
    val rawRows = fetch()
        .all()
        .collectList()
        .awaitSingle()

    return SimpleRows(rawRows)
}
