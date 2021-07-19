package server

import core.Request
import core.Response
import core.requesthandling.RequestHandlerEventManager

interface RequestHandler {
    val eventManager: RequestHandlerEventManager // Should this be nullable. To allow implementations that don't require it to opt out
    fun handleRequest(request: Request): Response
}

