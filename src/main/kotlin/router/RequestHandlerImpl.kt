package router

import server.RequestHandler
import core.Request
import core.Response
import intercepting.InterceptorManager

class RequestHandlerImpl(
    private val endpointFactory: EndpointFactory,
    private val interceptorManager: InterceptorManager,
    private val exceptionHandler: ExceptionHandler
) : RequestHandler {
    override fun handleRequest(request: Request): Response {
        val res: Response = try {
            val req = interceptorManager.interceptRequest(request)
            runEndpoint(req)
        } catch (e: Exception) {
            exceptionHandler.handleException(e, request)
        }

        return try {
            interceptorManager.interceptResponse(res)
        } catch (e: Exception) {
            exceptionHandler.handleException(e, request)
        }

        /*val req = interceptorManager.interceptRequest(request)
        val endpoint = endpointFactory.getEndpoint(request.actionCode)
        val res = endpoint.handleRequest(req)
        return interceptorManager.interceptResponse(res)*/
    }

    private fun runEndpoint(request: Request): Response {
        val endpoint = endpointFactory.getEndpoint(request.actionCode)
        return endpoint.handleRequest(request)
    }
}