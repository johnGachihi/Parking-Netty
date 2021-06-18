package modbus

import system.core.RequestHandler
import system.core.Request
import system.core.Response

class ModbusRequestHandler(
    private val endpointFactory: EndpointFactory
) : RequestHandler {
    override fun handleRequest(request: Request): Response {
        return endpointFactory.getEndpoint(request.actionCode)
            .handleRequest(request)
    }
}