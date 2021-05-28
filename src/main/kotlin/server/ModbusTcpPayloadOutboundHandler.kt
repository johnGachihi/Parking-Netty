package server

import com.digitalpetri.modbus.ExceptionCode
import com.digitalpetri.modbus.FunctionCode
import com.digitalpetri.modbus.codec.ModbusTcpPayload
import com.digitalpetri.modbus.requests.WriteMultipleRegistersRequest
import com.digitalpetri.modbus.responses.ExceptionResponse
import com.digitalpetri.modbus.responses.WriteMultipleRegistersResponse
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelOutboundHandlerAdapter
import io.netty.channel.ChannelPromise
import server.util.AttributeKeys

class ModbusTcpPayloadOutboundHandler : ChannelOutboundHandlerAdapter() {
    override fun write(ctx: ChannelHandlerContext, msg: Any?, promise: ChannelPromise?) {
        // Retrieve request payload
        val requestPayload = ctx.channel().attr(AttributeKeys.ModbusRequestPayload).get()

        when (requestPayload.modbusPdu.functionCode) {
            FunctionCode.WriteMultipleRegisters -> {
                val requestPdu = requestPayload.modbusPdu
                        as WriteMultipleRegistersRequest
                val responsePdu = WriteMultipleRegistersResponse(
                    requestPdu.address, requestPdu.quantity
                )
                val responsePayload = ModbusTcpPayload(
                    requestPayload.transactionId, requestPayload.unitId, responsePdu
                )
                ctx.writeAndFlush(responsePayload)
            }
            else -> {
                writeSlaveDeviceFailureException(ctx, requestPayload)
                throw Exception("Invalid function code")
            }
        }
    }

    private fun writeSlaveDeviceFailureException(
        ctx: ChannelHandlerContext,
        requestPayload: ModbusTcpPayload
    ) {
        val exceptionPdu = ExceptionResponse(
            requestPayload.modbusPdu.functionCode,
            ExceptionCode.SlaveDeviceFailure
        )
        val responsePayload = ModbusTcpPayload(
            requestPayload.transactionId,
            requestPayload.unitId,
            exceptionPdu
        )
        ctx.writeAndFlush(responsePayload)
    }
}