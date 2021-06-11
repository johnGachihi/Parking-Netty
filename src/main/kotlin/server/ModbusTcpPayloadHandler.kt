package server

import com.digitalpetri.modbus.ExceptionCode
import com.digitalpetri.modbus.FunctionCode
import com.digitalpetri.modbus.codec.ModbusTcpPayload
import com.digitalpetri.modbus.responses.ExceptionResponse
import core.decode.WriteRequestDecoder
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext

/*
Purpose:
- Block all request for all unsupported function codes
- Route requests to appropriate subsequent handlers

Why should we have separate Write and Read Handlers:
- Write requests require additional decoding of writeData

Let's decode the writeData here,
And do away with Write and Read Handlers
*/
// TODO: Rename to something like Modbus-to-Application Decoder
class ModbusTcpPayloadHandler(
    private val writeRequestDecoder: WriteRequestDecoder
) : ChannelDuplexHandler() {
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg !is ModbusTcpPayload)
            return // TODO: Should it return or throw?

        when (msg.modbusPdu.functionCode) {
            FunctionCode.WriteMultipleRegisters -> {
                ctx.fireChannelRead(writeRequestDecoder.decode(msg))
            }
            else -> writeException(ExceptionCode.IllegalFunction, ctx, msg)
        }
    }

























    /*override fun write(ctx: ChannelHandlerContext, msg: Any?, promise: ChannelPromise?) {
        when (requestPayload.modbusPdu.functionCode) {
            FunctionCode.WriteMultipleRegisters -> {
                val requestPdu = requestPayload.modbusPdu as WriteMultipleRegistersRequest
                val responsePdu = WriteMultipleRegistersResponse(
                    requestPdu.address, requestPdu.quantity
                )
                val responsePayload = ModbusTcpPayload(
                    requestPayload.transactionId,
                    requestPayload.unitId,
                    responsePdu
                )

                val future = ctx.writeAndFlush(responsePayload)
//                future.addListener(ChannelFutureListener.CLOSE)
            }
            else -> {
                writeException(ExceptionCode.SlaveDeviceFailure, ctx)
            }
        }
    }*/

    private fun writeException(
        exceptionCode: ExceptionCode,
        ctx: ChannelHandlerContext,
        requestPayload: ModbusTcpPayload
    ) {
        val exceptionPdu = ExceptionResponse(
            requestPayload.modbusPdu.functionCode,
            exceptionCode
        )
        val responsePayload = ModbusTcpPayload(
            requestPayload.transactionId,
            requestPayload.unitId,
            exceptionPdu
        )
        ctx.writeAndFlush(responsePayload)
    }
}