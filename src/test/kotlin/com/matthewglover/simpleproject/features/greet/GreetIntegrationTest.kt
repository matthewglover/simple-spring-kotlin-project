package com.matthewglover.simpleproject.features.greet

import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

class GreetIntegrationTest {

    @Test
    fun `a valid request returns a valid greeting response`() {
        val webClient = WebTestClient
            .bindToRouterFunction(RouteConfig().greetRoute())
            .build();

        webClient
            .get().uri("/greet")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<Greeting>().isEqualTo(Greeting("Hello, world!"))
    }
}
