package system.core

interface Endpoint {
    fun handleRequest(request: Request): Response
}