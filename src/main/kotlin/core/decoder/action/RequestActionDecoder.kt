package core.decoder.action

import com.digitalpetri.modbus.codec.ModbusTcpPayload
import core.RequestAction

@Deprecated("Wait to see if there will be any uses.")
interface RequestActionDecoder {
    fun getAction(modbusTcpPayload: ModbusTcpPayload): RequestAction
}