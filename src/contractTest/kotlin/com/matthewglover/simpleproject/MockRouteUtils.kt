package com.matthewglover.simpleproject

import com.matthewglover.simpleproject.common.routeutils.RouteUtils
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl

class MockRouteUtils : RouteUtils {

    override fun coRouter(routes: CoRouterFunctionDsl.() -> Unit) =
        org.springframework.web.reactive.function.server.coRouter {
            routes(this)
        }
}
