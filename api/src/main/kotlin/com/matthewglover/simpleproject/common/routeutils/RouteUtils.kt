package com.matthewglover.simpleproject.common.routeutils

import com.matthewglover.simpleproject.common.logging.RequestLoggingContextCreator
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse

interface RouteUtils {
    fun coRouter(routes: (CoRouterFunctionDsl.() -> Unit)): RouterFunction<ServerResponse>
}

@Component
class RouteUtilsImpl(
    private val requestLoggingContextCreator: RequestLoggingContextCreator,
    private val requestResponseLogging: RequestResponseLogging
) : RouteUtils {

    override fun coRouter(routes: (CoRouterFunctionDsl.() -> Unit)) =
        org.springframework.web.reactive.function.server.coRouter {
            routes()

            filter(ExceptionHandling::applyToRoutes)
            filter(requestLoggingContextCreator::writeRequestLoggingContext)
            filter(requestResponseLogging::filter)
        }
}
