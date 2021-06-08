package core

import com.digitalpetri.modbus.requests.WriteMultipleRegistersRequest
import io.netty.buffer.ByteBuf

class WriteRequest(modbusRequestPdu: WriteMultipleRegistersRequest) : Request {
    val writeData: ByteBuf = modbusRequestPdu.values
    override val actionCode: Int = modbusRequestPdu.address
}