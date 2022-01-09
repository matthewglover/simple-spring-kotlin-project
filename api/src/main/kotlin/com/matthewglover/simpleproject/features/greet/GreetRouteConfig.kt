package com.matthewglover.simpleproject.features.greet

import com.matthewglover.simpleproject.common.response.asOk
import com.matthewglover.simpleproject.common.routeutils.RouteUtils
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springdoc.core.annotations.RouterOperation
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
class GreetRouteConfig {

    @Bean
    @RouterOperation(
        operation = Operation(
            operationId = "greet",
            summary = "Greet person",
            responses = [
                ApiResponse(
                    responseCode = "200",
                    description = "greet person",
                    content = [Content(schema = Schema(implementation = Greeting::class))]
                )
            ]
        ),
    )
    fun greetRoute(routeUtils: RouteUtils) = routeUtils.coRouter {
        GET("/greet", ::handleGreet)
    }

    @Suppress("UnusedPrivateMember")
    suspend fun handleGreet(request: ServerRequest): ServerResponse =
        Greeting("Hello, world!").asOk()
}

data class Greeting(val message: String)
