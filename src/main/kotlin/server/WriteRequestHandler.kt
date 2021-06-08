package server

import core.RequestAction
import core.WriteRequest
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

class WriteRequestHandler : SimpleChannelInboundHandler<WriteRequest>() {
    override fun channelRead0(ctx: ChannelHandlerContext, writeRequest: WriteRequest) {
        val action = RequestAction.fromCode(writeRequest.actionCode)
        when (action) {
            RequestAction.Exit -> { /* Decode writeData */ }
//            RequestAction.Entry -> { /* Decode writeData */ }
        }
    }
}