package com.matthewglover.simpleproject.features.greet

import com.matthewglover.simpleproject.utils.MockRouteUtils
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

class GreetRouteHandlerTest {

    @Test
    fun `a valid request returns a valid greeting response`() {
        val routeUtils = MockRouteUtils()

        val webClient = WebTestClient
            .bindToRouterFunction(GreetRouteConfig().greetRoute(routeUtils))
            .build()

        webClient
            .get().uri("/greet")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<Greeting>().isEqualTo(Greeting("Hello, world!"))
    }
}
