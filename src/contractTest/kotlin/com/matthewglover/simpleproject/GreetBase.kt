package com.matthewglover.simpleproject

import com.matthewglover.simpleproject.features.greet.GreetRouteConfig
import io.restassured.module.webtestclient.RestAssuredWebTestClient
import org.junit.jupiter.api.BeforeEach

open class GreetBase {

    @BeforeEach
    fun setup() {
        val greetRoute = GreetRouteConfig().greetRoute(TestRouteUtils.routeUtils())
        RestAssuredWebTestClient.standaloneSetup(greetRoute)
    }
}
