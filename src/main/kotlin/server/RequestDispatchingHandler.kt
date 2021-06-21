package server

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import core.Request

class RequestDispatchingHandler(
    private val requestHandler: RequestHandler
) : SimpleChannelInboundHandler<Request>() {
    override fun channelRead0(ctx: ChannelHandlerContext, msg: Request) {
        val response = requestHandler.handleRequest(msg)
        ctx.writeAndFlush(response)
    }
}