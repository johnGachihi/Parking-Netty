package router

import server.RequestHandler
import core.Request
import core.Response

class RequestHandlerImpl(
    private val endpointFactory: EndpointFactory
) : RequestHandler {
    override fun handleRequest(request: Request): Response {
        return endpointFactory.getEndpoint(request.actionCode)
            .handleRequest(request)
    }
}