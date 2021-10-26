package com.matthewglover.simpleproject

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class RouteConfiguration {

    @Bean
    fun routes(greetHandler: GreetHandler): RouterFunction<ServerResponse> =
        coRouter {
            GET("/greet", greetHandler::greet)
        }
}
