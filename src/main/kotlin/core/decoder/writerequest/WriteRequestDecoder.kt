package core.decoder.writerequest

import com.digitalpetri.modbus.codec.ModbusTcpPayload
import core.WriteRequest

interface WriteRequestDecoder {
    fun decode(modbusTcpPayload: ModbusTcpPayload): WriteRequest
}