package router

interface EndpointFactory {
    fun getEndpoint(actionCode: Int): Endpoint
}

class EndpointFactoryImpl(
    private val actionToEndpointMap: Map<Int, Endpoint>
) : EndpointFactory {
    override fun getEndpoint(actionCode: Int): Endpoint {
        return actionToEndpointMap[actionCode]
            ?: throw UnsupportedActionException("$actionCode")
    }
}

