package com.matthewglover.simpleproject.contracts

import com.matthewglover.simpleproject.common.routeutils.RouteUtils
import com.matthewglover.simpleproject.features.greet.GreetRouteConfig
import io.restassured.module.webtestclient.RestAssuredWebTestClient
import org.junit.jupiter.api.BeforeEach
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl

open class ContractVerifierBase {

    @BeforeEach
    fun setup() {
        val greetRoute = GreetRouteConfig().greetRoute(MockRouteUtils())
        RestAssuredWebTestClient.standaloneSetup(greetRoute)
    }
}

class MockRouteUtils : RouteUtils {

    override fun coRouter(routes: CoRouterFunctionDsl.() -> Unit) =
        org.springframework.web.reactive.function.server.coRouter {
            routes(this)
        }
}
