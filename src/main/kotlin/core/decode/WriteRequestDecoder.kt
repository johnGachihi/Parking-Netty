package core.decode

import com.digitalpetri.modbus.codec.ModbusTcpPayload
import com.digitalpetri.modbus.requests.WriteMultipleRegistersRequest
import core.RequestAction
import core.UnsupportedActionException
import core.WriteRequest

interface WriteRequestDecoder {
    fun decode(modbusTcpPayload: ModbusTcpPayload): WriteRequest
}

class WriteRequestDecoderImpl(
    private val registry: WriteDataDecoderRegistry
) : WriteRequestDecoder {
    override fun decode(modbusTcpPayload: ModbusTcpPayload): WriteRequest {
        val modbusPdu = modbusTcpPayload.modbusPdu as WriteMultipleRegistersRequest
        val action = RequestAction.fromCode(modbusPdu.address)

        val writeDataDecoder = registry.getDecoder(action)
            ?: throw UnsupportedActionException()

        val writeData = writeDataDecoder.decode(modbusPdu.values)
        return WriteRequest(action, writeData, modbusTcpPayload)
    }
}