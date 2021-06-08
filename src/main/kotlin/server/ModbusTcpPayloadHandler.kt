package server

import com.digitalpetri.modbus.ExceptionCode
import com.digitalpetri.modbus.FunctionCode
import com.digitalpetri.modbus.codec.ModbusTcpPayload
import com.digitalpetri.modbus.requests.*
import com.digitalpetri.modbus.responses.ExceptionResponse
import com.digitalpetri.modbus.responses.WriteMultipleRegistersResponse
import com.digitalpetri.modbus.responses.WriteSingleCoilResponse
import core.WriteRequest
import core.decoder.writerequest.WriteRequestDecoder
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import io.netty.util.CharsetUtil

/*
Purpose:
- Block all request for all unsupported function codes
- Route requests to appropriate subsequent handlers

Why should we have separate Write and Read Handlers:
- Write requests require additional decoding of writeData

Let's decode the writeData here,
And do away with Write and Read Handlers
*/
class ModbusTcpPayloadHandler(
    private val writeRequestDecoder: WriteRequestDecoder
) : ChannelDuplexHandler() {
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg !is ModbusTcpPayload)
            return // TODO: Should it return or throw?

        when (msg.modbusPdu.functionCode) {
            FunctionCode.WriteMultipleRegisters -> {
                ctx.fireChannelRead(writeRequestDecoder.decode(msg))
                /*
                ** Inside WriteRequestDecoder **
                * - What happens when the action is not recognized
                * ** ** ** ** ** ** ** ** ** ***
                val writeRequest = writeRequestDecoder.decode(msg)
                ctx.fireChannelRead(writeRequest)

                val decodedData = writeDataDecoder.decode(data, action)

                */
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