package modbus

import com.digitalpetri.modbus.codec.ModbusTcpPayload
import com.digitalpetri.modbus.requests.WriteMultipleRegistersRequest
import modbus.core.ModbusResponse
import modbus.core.ModbusWriteRequest
import system.core.Request
import system.core.RequestCodec
import system.core.Response

class ModbusRequestCodec : RequestCodec<ModbusTcpPayload> {
    override fun decode(protocolMsg: ModbusTcpPayload): Request {
        return when (val modbusPdu = protocolMsg.modbusPdu) {
            is WriteMultipleRegistersRequest -> {
                // The action code for modbus requests is the address.
                val actionCode = modbusPdu.address
                ModbusWriteRequest(actionCode, protocolMsg)
            }
            else -> throw UnsupportedFunctionException(modbusPdu.functionCode.code)
        }
    }

    override fun encode(response: Response): ModbusTcpPayload {
        response as ModbusResponse
        return response.modbusTcpPayload
    }
}