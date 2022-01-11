package com.matthewglover.simpleproject.common.response

import com.matthewglover.simpleproject.common.errors.ApplicationError
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.coRouter

fun testError(error: ApplicationError): WebTestClient.ResponseSpec {
    val webClient = setupWebClient(error)

    return webClient
        .get().uri("/test")
        .exchange()
}

private fun setupWebClient(error: ApplicationError) =
    WebTestClient
        .bindToRouterFunction(testRoute(error))
        .build()

private fun testRoute(error: ApplicationError) = coRouter {
    accept(MediaType.APPLICATION_JSON).nest {
        GET("/test") { error.asError() }
    }
}
