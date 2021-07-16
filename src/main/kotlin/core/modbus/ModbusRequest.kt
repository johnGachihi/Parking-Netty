package core.modbus

import com.digitalpetri.modbus.codec.ModbusTcpPayload
import com.digitalpetri.modbus.requests.WriteMultipleRegistersRequest
import io.netty.buffer.ByteBuf
import core.Request
import exceptionhandling.ExceptionHandlingStrategy
import exceptionhandling.modbus.ModbusTcpExceptionHandlingStrategy

interface ModbusRequest : Request {
    val modbusTcpPayload: ModbusTcpPayload
}

/*
* TODO
* Replace ModbusTcpPayload from the parameters
* with:
*   1. unit id
*   2. transaction id
*   3. WriteMultipleRegisterPayload or its contents, which are:
*        i.  memory address
*       ii. quantity
*      iii. the data to be written (bytes)
*
* The change could start with creating a new primary constructor,
* then making the current primary constructor secondary and
* deprecating it.
* */
class ModbusWriteRequest(
    override val actionCode: Int,
    override val modbusTcpPayload: ModbusTcpPayload
) : ModbusRequest {
    val data: ByteBuf = (modbusTcpPayload.modbusPdu as WriteMultipleRegistersRequest).values

    override val exceptionHandlingStrategy: ExceptionHandlingStrategy =
        ModbusTcpExceptionHandlingStrategy()
}