package router

import core.Request
import core.Response
import db.HibernateSessionContextManager
import server.RequestHandler

class RequestHandlerImpl(
    private val endpointFactory: EndpointFactory,
    private val hibernateSessionContextManager: HibernateSessionContextManager,
    private val exceptionHandler: ExceptionHandler
) : RequestHandler {
    override fun handleRequest(request: Request): Response {
        hibernateSessionContextManager.beginSessionContext()

        var response: Response
        try {
            response = runEndpoint(request)

            hibernateSessionContextManager.closeSessionContext()
        } catch (e: Exception) {
            response = exceptionHandler.handleException(e, request)

            hibernateSessionContextManager.closeSessionContextExceptionally()
        }
        return response
    }

    private fun runEndpoint(request: Request): Response {
        val endpoint = endpointFactory.getEndpoint(request.actionCode)
        return endpoint.handleRequest(request)
    }
}