package com.matthewglover.simpleproject.features.users

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springdoc.core.annotations.RouterOperation
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class UserRouteConfig {

    @Bean
    @RouterOperation(
        operation = Operation(
            operationId = "get-user",
            summary = "Get user",
            responses = [
                ApiResponse(
                    responseCode = "200",
                    description = "gets a user by userId",
                    content = [Content(schema = Schema(implementation = User::class))]
                )
            ]
        ),
    )
    fun getUserRoute(userHandlers: UserHandlers) = coRouter {
        GET("/users/{userId}", userHandlers::handleGetUser)
    }
}
