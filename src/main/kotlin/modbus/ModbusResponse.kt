package modbus

import com.digitalpetri.modbus.codec.ModbusTcpPayload
import core.Response

class ModbusResponse(val modbusTcpPayload: ModbusTcpPayload) : Response