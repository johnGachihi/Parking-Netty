package server

import core.Request
import core.WriteRequest
import endpoints.EndpointRegistry
import endpoints.WriteRequestEndpoint
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import java.lang.IllegalStateException

class RequestDispatchingHandler(
    private val endpointRegistry: EndpointRegistry
) : SimpleChannelInboundHandler<Request>() {
    override fun channelRead0(ctx: ChannelHandlerContext?, request: Request?) {
        when (request) {
            is WriteRequest -> {
                val endpoint = endpointRegistry.getEndpoint(request.action)
                    ?: throw IllegalStateException() // TODO: Check this
                (endpoint as WriteRequestEndpoint).handleRequest(request)
            }
        }
    }
}