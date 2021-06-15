package endpoints

import core.WriteRequest

interface Endpoint

interface WriteRequestEndpoint : Endpoint {
    fun handleRequest(writeRequest: WriteRequest)
}

// ReadRequestEndpoint