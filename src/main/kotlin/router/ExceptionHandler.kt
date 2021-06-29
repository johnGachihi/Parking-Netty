package router

import com.digitalpetri.modbus.ExceptionCode
import com.digitalpetri.modbus.codec.ModbusTcpPayload
import com.digitalpetri.modbus.responses.ExceptionResponse
import core.Request
import core.Response
import core.modbus.ModbusResponse
import core.modbus.ModbusWriteRequest

interface ExceptionHandler {
    fun handleException(exc: Exception, req: Request): Response
}

class ExceptionHandlerImpl : ExceptionHandler {
    override fun handleException(exc: Exception, req: Request): Response {
        req as ModbusWriteRequest
        val modbusPayload = req.modbusTcpPayload

        val responsePdu = ExceptionResponse(
            modbusPayload.modbusPdu.functionCode,
            ExceptionCode.SlaveDeviceFailure
        )
        val modbusTcpPayload = ModbusTcpPayload(
            modbusPayload.transactionId,
            modbusPayload.unitId,
            responsePdu
        )

        return ModbusResponse(modbusTcpPayload)
    }
}

