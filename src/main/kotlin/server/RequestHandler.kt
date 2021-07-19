package server

import core.Request
import core.Response
import core.requesthandling.RequestHandlerEventManager

interface RequestHandler {
    val eventManager: RequestHandlerEventManager
    fun handleRequest(request: Request): Response
}

