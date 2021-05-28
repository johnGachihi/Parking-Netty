package server

import com.digitalpetri.modbus.FunctionCode
import com.digitalpetri.modbus.codec.ModbusTcpPayload
import com.digitalpetri.modbus.requests.WriteMultipleRegistersRequest
import core.WriteRequest
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise

class ModbusTcpPayloadHandler : ChannelDuplexHandler() {
    lateinit var requestPayload: ModbusTcpPayload

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        requestPayload = msg as ModbusTcpPayload

        when (requestPayload.modbusPdu.functionCode) {
            FunctionCode.WriteMultipleRegisters -> {
                ctx.pipeline().addAfter(ctx.name(), "writeRequestHandler", WriteRequestHandler())

                val writeData = (requestPayload.modbusPdu as WriteMultipleRegistersRequest).values
                ctx.fireChannelRead(WriteRequest(writeData))
            }
            else -> {

            }
        }
    }

    override fun write(ctx: ChannelHandlerContext?, msg: Any?, promise: ChannelPromise?) {
        super.write(ctx, msg, promise)
    }
}