package system.core

import system.core.Request
import system.core.Response

interface RequestHandler {
    fun handleRequest(request: Request): Response
}