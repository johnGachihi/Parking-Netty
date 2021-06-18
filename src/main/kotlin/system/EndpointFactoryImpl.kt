package system

import modbus.EndpointFactory
import system.core.Endpoint

class EndpointFactoryImpl(
    private val actionToEndpointMap: Map<Int, Endpoint>
) : EndpointFactory {
    override fun getEndpoint(actionCode: Int): Endpoint {
        return actionToEndpointMap[actionCode]
            ?: throw UnsupportedActionException("$actionCode")
    }
}