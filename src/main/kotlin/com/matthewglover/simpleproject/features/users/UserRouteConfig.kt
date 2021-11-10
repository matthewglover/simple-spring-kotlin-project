package com.matthewglover.simpleproject.features.users

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springdoc.core.annotations.RouterOperation
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class UserRouteConfig {

    @Bean
    @RouterOperation(
        beanClass = UserService::class,
        beanMethod = "getUser",
        operation = Operation(
            operationId = "get-user",
            summary = "Get a user",
            responses = [
                ApiResponse(
                    responseCode = "200",
                    description = "gets user for given userId",
                ),
                ApiResponse(
                    responseCode = "404",
                    description = "no user for given userId",
                    content = [Content()]
                ),
            ]
        ),
    )
    fun getUserRoute(userHandlers: UserHandlers) = coRouter {
        GET("/users/{userId}", userHandlers::handleGetUser)
    }

    @Bean
    @RouterOperation(
        beanClass = UserService::class,
        beanMethod = "addUser",
        operation = Operation(
            operationId = "add-user",
            summary = "Add a new user",
            responses = [
                ApiResponse(
                    responseCode = "200",
                    description = "adds and returns new user",
                ),
            ]
        ),
    )
    fun addUserRoute(userHandlers: UserHandlers) = coRouter {
        accept(MediaType.APPLICATION_JSON).nest {
            POST("/users", userHandlers::handleAddUser)
        }
    }
}
