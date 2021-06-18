package system.server

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageCodec
import system.core.RequestCodec
import system.core.Response

class ProtocolToAppRequestCodec<TO_DECODE>(
    private val requestCodec: RequestCodec<TO_DECODE>,
    inboundMessageType: Class<TO_DECODE>
) : MessageToMessageCodec<TO_DECODE, Response>(inboundMessageType, Response::class.java) {

    override fun decode(ctx: ChannelHandlerContext?, msg: TO_DECODE, out: MutableList<Any>) {
        out.add(requestCodec.decode(msg))
    }

    override fun encode(ctx: ChannelHandlerContext?, msg: Response, out: MutableList<Any>) {
        out.add(requestCodec.encode(msg) as Any)
    }
}