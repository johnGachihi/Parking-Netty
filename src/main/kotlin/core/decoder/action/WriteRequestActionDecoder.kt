package core.decoder.action

import com.digitalpetri.modbus.codec.ModbusTcpPayload
import com.digitalpetri.modbus.requests.WriteMultipleRegistersRequest
import core.RequestAction

@Deprecated("Wait to see if there will be any uses.")
class WriteRequestActionDecoder : RequestActionDecoder {
    override fun getAction(modbusTcpPayload: ModbusTcpPayload): RequestAction {
        val pdu = modbusTcpPayload.modbusPdu as WriteMultipleRegistersRequest
        return RequestAction.fromCode(pdu.address)
    }
}