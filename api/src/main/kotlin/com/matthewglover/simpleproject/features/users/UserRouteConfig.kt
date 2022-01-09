package com.matthewglover.simpleproject.features.users

import com.matthewglover.simpleproject.common.routeutils.RouteUtils
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMethod

@Configuration
class UserRouteConfig {

    @RouterOperations(
        value = [
            RouterOperation(
                path = "/users/{userId}",
                method = [RequestMethod.GET],
                beanClass = UserHandlers::class,
                beanMethod = "handleGetUser",
                operation = Operation(
                    operationId = "get-user",
                    summary = "Get a user by userId",
                    parameters = [Parameter(name = "userId", `in` = ParameterIn.PATH)],
                    responses = [
                        ApiResponse(
                            responseCode = "200",
                            description = "returns user for given userId",
                        ),
                        ApiResponse(
                            responseCode = "404",
                            description = "no user for given userId",
                            content = [Content()]
                        ),
                    ]
                )
            ),
            RouterOperation(
                path = "/users/",
                method = [RequestMethod.POST],
                beanClass = UserHandlers::class,
                beanMethod = "handleAddUser",
                operation = Operation(
                    operationId = "add-user",
                    summary = "Add a new user",
                    method = "POST",
                    requestBody = RequestBody(
                        content = [Content(schema = Schema(implementation = NewUser::class))]
                    ),
                    responses = [
                        ApiResponse(
                            responseCode = "200",
                            description = "returns newly created user",
                        ),
                        ApiResponse(
                            responseCode = "400",
                            description = "invalid request",
                        ),
                        ApiResponse(
                            responseCode = "415",
                            description = "unsupported media type",
                        ),
                    ]
                ),
            )
        ]
    )
    @Bean
    fun userRoutes(routeUtils: RouteUtils, userHandlers: UserHandlers) = routeUtils.coRouter {
        GET("/users/{userId}", userHandlers::handleGetUser)

        accept(MediaType.APPLICATION_JSON).nest {
            POST("/users", userHandlers::handleAddUser)
        }
    }
}
