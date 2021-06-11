package core

import com.digitalpetri.modbus.codec.ModbusTcpPayload
import com.digitalpetri.modbus.requests.WriteMultipleRegistersRequest
import com.digitalpetri.modbus.responses.WriteMultipleRegistersResponse

interface Request {
    val action: RequestAction
}

class WriteRequest(
    override val action: RequestAction,
    val data: Any,
    private val modbusTcpPayload: ModbusTcpPayload
) : Request
{
    fun getModbusResponse(): ModbusTcpPayload {
        val requestPdu = modbusTcpPayload.modbusPdu as WriteMultipleRegistersRequest
        val responsePdu = WriteMultipleRegistersResponse(
            requestPdu.address, requestPdu.quantity
        )
        return ModbusTcpPayload(
            modbusTcpPayload.transactionId,
            modbusTcpPayload.unitId,
            responsePdu
        )
    }
}