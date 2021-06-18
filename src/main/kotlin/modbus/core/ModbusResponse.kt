package modbus.core

import com.digitalpetri.modbus.codec.ModbusTcpPayload
import system.core.Response

class ModbusResponse(val modbusTcpPayload: ModbusTcpPayload) : Response