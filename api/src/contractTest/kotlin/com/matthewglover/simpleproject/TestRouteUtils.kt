package com.matthewglover.simpleproject

import com.matthewglover.simpleproject.common.logging.ContextLoggerFactory
import com.matthewglover.simpleproject.common.logging.RequestLoggingContextCreator
import com.matthewglover.simpleproject.common.logging.RequestResponseLogging
import com.matthewglover.simpleproject.common.routeutils.RouteUtils
import com.matthewglover.simpleproject.common.routeutils.RouteUtilsImpl

object TestRouteUtils {

    fun routeUtils(): RouteUtils {
        val contextLoggerFactory = ContextLoggerFactory()
        val requestResponseLogging = RequestResponseLogging(contextLoggerFactory)

        val requestLoggingContextCreator = RequestLoggingContextCreator()

        return RouteUtilsImpl(requestLoggingContextCreator, requestResponseLogging)
    }
}
