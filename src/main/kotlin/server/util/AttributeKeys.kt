package server.util

import com.digitalpetri.modbus.codec.ModbusTcpPayload
import io.netty.util.AttributeKey

object AttributeKeys {
    val ModbusRequestPayload: AttributeKey<ModbusTcpPayload> =
        AttributeKey.newInstance("ModbusRequestPayload")
}