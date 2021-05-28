package server

import com.digitalpetri.modbus.ExceptionCode
import com.digitalpetri.modbus.FunctionCode
import com.digitalpetri.modbus.codec.ModbusTcpPayload
import com.digitalpetri.modbus.requests.WriteMultipleRegistersRequest
import com.digitalpetri.modbus.responses.ExceptionResponse
import core.WriteRequest
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import server.util.AttributeKeys

class ModbusTcpPayloadInboundHandler : SimpleChannelInboundHandler<ModbusTcpPayload>() {
    override fun channelRead0(ctx: ChannelHandlerContext, payload: ModbusTcpPayload) {
        when (payload.modbusPdu.functionCode) {
            FunctionCode.WriteMultipleRegisters -> {
                // Store request payload
                ctx.channel().attr(AttributeKeys.ModbusRequestPayload).set(payload)

                ctx.pipeline().addLast(WriteRequestHandler())

                val writeData = (payload.modbusPdu as WriteMultipleRegistersRequest).values
                ctx.fireChannelRead(WriteRequest(writeData))
            }
            else -> {
                writeIllegalFunctionCodeExceptionResponse(ctx, payload)
            }
        }
    }

    private fun writeIllegalFunctionCodeExceptionResponse(
        ctx: ChannelHandlerContext,
        payload: ModbusTcpPayload
    ) {
        val exceptionResponse = ExceptionResponse(
            payload.modbusPdu.functionCode,
            ExceptionCode.IllegalFunction
        )
        val modbusTcpPayload = ModbusTcpPayload(
            payload.transactionId, payload.unitId, exceptionResponse
        )
        ctx.writeAndFlush(modbusTcpPayload)
    }
}