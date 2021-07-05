package router

import core.Request
import core.Response
import db.HibernateSessionContextManager
import server.RequestHandler

class RequestHandlerImpl(
    private val endpointFactory: EndpointFactory,
    private val hibernateSessionContextManager: HibernateSessionContextManager, // Smell
    private val exceptionHandler: ExceptionHandler
) : RequestHandler {
    override fun handleRequest(request: Request): Response {
        hibernateSessionContextManager.beginSessionContext()

        return try {
            val response = runEndpoint(request)

            hibernateSessionContextManager.closeSessionContext()

            response
        } catch (e: Exception) {
            val response = exceptionHandler.handleException(e, request)

            hibernateSessionContextManager.closeSessionContextExceptionally()

            response
        }
    }

    private fun runEndpoint(request: Request): Response {
        val endpoint = endpointFactory.getEndpoint(request.actionCode)
        return endpoint.handleRequest(request)
    }
}