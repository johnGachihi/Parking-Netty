package core.router

interface EndpointFactory {
    fun getEndpoint(actionCode: Int): Endpoint
}