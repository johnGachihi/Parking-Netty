package app.endpoints

import com.digitalpetri.modbus.codec.ModbusTcpPayload
import com.digitalpetri.modbus.requests.WriteMultipleRegistersRequest
import core.Request
import core.Response
import modbus.ModbusWriteRequest
import io.netty.buffer.ByteBuf
import server.RequestHandler
import kotlin.math.ceil

class MockServer(
    private val requestHandler: RequestHandler,
) {
    fun sendModbusWriteRequest(address: Int, data: ByteBuf): Response {
        val request = formModbusWriteRequest(address, data)
        return handleRequest(request)
    }

    private fun formModbusWriteRequest(address: Int, data: ByteBuf): ModbusWriteRequest {
        val pdu = WriteMultipleRegistersRequest(
            address,
            ceil(data.readableBytes() / 2.0).toInt(),
            data
        )
        val modbusTcpPayload = ModbusTcpPayload(1, 1, pdu)
        return ModbusWriteRequest(address, modbusTcpPayload)
    }

    private fun handleRequest(request: Request): Response {
        return requestHandler.handleRequest(request)
    }
}