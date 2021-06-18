package modbus

import com.digitalpetri.modbus.codec.ModbusTcpPayload
import com.digitalpetri.modbus.requests.WriteMultipleRegistersRequest
import com.digitalpetri.modbus.responses.WriteMultipleRegistersResponse
import modbus.core.Decoder
import modbus.core.ModbusResponse
import modbus.core.ModbusWriteRequest
import system.core.Endpoint
import system.core.Request
import system.core.Response

abstract class WriteRequestEndpoint<T> : Endpoint {
    override fun handleRequest(request: Request): Response {
        println(request.actionCode)
        request as ModbusWriteRequest
        val decodedData = createDecoder().decode(request.data)
        handleRequest(decodedData)
        return createResponse(request)
    }

    private fun createResponse(request: ModbusWriteRequest): Response {
        val requestPayload = request.modbusTcpPayload
        val requestPdu = requestPayload.modbusPdu as WriteMultipleRegistersRequest
        val responsePdu = WriteMultipleRegistersResponse(
            requestPdu.address, requestPdu.quantity
        )
        val modbusTcpPayload = ModbusTcpPayload(
            requestPayload.transactionId,
            requestPayload.unitId,
            responsePdu
        )

        return ModbusResponse(modbusTcpPayload)
    }

    protected abstract fun handleRequest(data: T)
    protected abstract fun createDecoder(): Decoder<T>
}