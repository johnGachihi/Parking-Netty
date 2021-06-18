package modbus

import system.UnsupportedActionException
import system.core.Endpoint

interface EndpointFactory {
    fun getEndpoint(actionCode: Int): Endpoint
}

