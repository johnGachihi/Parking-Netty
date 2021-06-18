package modbus.core

import com.digitalpetri.modbus.codec.ModbusTcpPayload
import com.digitalpetri.modbus.requests.WriteMultipleRegistersRequest
import io.netty.buffer.ByteBuf
import system.core.Request

class ModbusWriteRequest(
    override val actionCode: Int,
    val modbusTcpPayload: ModbusTcpPayload
) : Request {
    val data: ByteBuf = (modbusTcpPayload.modbusPdu as WriteMultipleRegistersRequest).values
}