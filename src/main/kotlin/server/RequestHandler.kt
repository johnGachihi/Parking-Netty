package server

import core.Request
import core.Response

interface RequestHandler {
    fun handleRequest(request: Request): Response
}

