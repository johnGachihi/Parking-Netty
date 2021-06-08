package core

import com.digitalpetri.modbus.codec.ModbusTcpPayload

interface Request {
 val actionCode: Int
// fun getModbusResponsePayload(): ModbusTcpPayload
}