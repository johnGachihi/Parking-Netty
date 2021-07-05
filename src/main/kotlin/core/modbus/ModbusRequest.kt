package core.modbus

import com.digitalpetri.modbus.codec.ModbusTcpPayload
import com.digitalpetri.modbus.requests.WriteMultipleRegistersRequest
import io.netty.buffer.ByteBuf
import core.Request

interface ModbusRequest : Request

class ModbusWriteRequest(
    override val actionCode: Int,
    val modbusTcpPayload: ModbusTcpPayload
) : ModbusRequest {
    val data: ByteBuf = (modbusTcpPayload.modbusPdu as WriteMultipleRegistersRequest).values
}