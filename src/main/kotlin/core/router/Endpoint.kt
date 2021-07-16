package core.router

import core.Request
import core.Response

interface Endpoint {
    fun handleRequest(request: Request): Response
}