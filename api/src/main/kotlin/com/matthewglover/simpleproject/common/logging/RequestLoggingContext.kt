package com.matthewglover.simpleproject.common.logging

import org.springframework.http.HttpMethod
import org.springframework.http.server.RequestPath

data class RequestLoggingContext(
    val correlationId: String,
    val startTime: Long,
    val method: HttpMethod?,
    val requestPath: RequestPath
) {
    fun toTags() = Tags(
        "correlationId" to correlationId,
        "method" to method.toString(),
        "requestPath" to requestPath.toString(),
        "startTime" to startTime.toString()
    )
}
