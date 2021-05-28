package server

import core.WriteRequest
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

class WriteRequestHandler : SimpleChannelInboundHandler<WriteRequest>() {
    override fun channelRead0(ctx: ChannelHandlerContext, msg: WriteRequest) {
        ctx.write("Hullabaloo")
    }
}