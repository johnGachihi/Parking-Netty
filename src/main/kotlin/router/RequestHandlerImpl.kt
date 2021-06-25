package router

import server.RequestHandler
import core.Request
import core.Response
import intercepting.InterceptorManager

class RequestHandlerImpl(
    private val endpointFactory: EndpointFactory,
    private val interceptorManager: InterceptorManager
) : RequestHandler {
    override fun handleRequest(request: Request): Response {
        val req = interceptorManager.interceptRequest(request)
        val endpoint = endpointFactory.getEndpoint(request.actionCode)
        val res = endpoint.handleRequest(req)
        return interceptorManager.interceptResponse(res)
    }
}