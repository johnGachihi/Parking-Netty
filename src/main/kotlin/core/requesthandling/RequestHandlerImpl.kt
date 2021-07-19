package core.requesthandling

import core.Request
import core.Response
import server.RequestHandler
import core.exceptionhandling.ExceptionHandler
import core.router.EndpointFactory

class RequestHandlerImpl(
    private val endpointFactory: EndpointFactory, // TODO: Create Router to find, instantiate and run Endpoint
    private val exceptionHandler: ExceptionHandler,
    override val eventManager: RequestHandlerEventManager = RequestHandlerEventManager()
) : RequestHandler {

    override fun handleRequest(request: Request): Response {
        eventManager.notifyRequestReceived()

        return try {
            val response = runEndpoint(request)

            eventManager.notifyRequestHandled()

            response
        } catch (e: Exception) {
            val response = exceptionHandler.handleException(e, request)

            eventManager.notifyRequestHandleExceptionally()

            response
        }
    }

    private fun runEndpoint(request: Request): Response {
        val endpoint = endpointFactory.getEndpoint(request.actionCode)
        return endpoint.handleRequest(request)
    }
}