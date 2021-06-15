package endpoints

import core.RequestAction

interface EndpointRegistry {
    fun register(action: RequestAction, endpoint: Endpoint)
    fun getEndpoint(action: RequestAction): Endpoint?
}

class EndpointRegistryImpl : EndpointRegistry {
    private val endpoints:  MutableMap<RequestAction, Endpoint> = mutableMapOf()

    override fun register(action: RequestAction, endpoint: Endpoint) {
        endpoints[action] = endpoint
    }

    override fun getEndpoint(action: RequestAction): Endpoint? =
        endpoints[action]
}