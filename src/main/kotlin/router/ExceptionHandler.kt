package router

import app.IllegalDataException
import app.UnservicedFeeException
import com.digitalpetri.modbus.ExceptionCode
import com.digitalpetri.modbus.codec.ModbusTcpPayload
import com.digitalpetri.modbus.responses.ExceptionResponse
import core.Request
import core.Response
import core.modbus.ModbusResponse
import core.modbus.ModbusWriteRequest
import io.netty.handler.codec.DecoderException

interface ExceptionHandler {
    fun handleException(exc: Exception, req: Request): Response
}

class ExceptionHandlerImpl : ExceptionHandler {
    override fun handleException(exc: Exception, req: Request): Response {
        exc.printStackTrace()
        req as ModbusWriteRequest
        val modbusPayload = req.modbusTcpPayload

        val responsePdu = if (exc is DecoderException || exc is IllegalDataException || exc is UnservicedFeeException) {
            ExceptionResponse(
                modbusPayload.modbusPdu.functionCode,
                ExceptionCode.IllegalDataValue
            )
        } else {
            ExceptionResponse(
                modbusPayload.modbusPdu.functionCode,
                ExceptionCode.SlaveDeviceFailure
            )
        }

        val modbusTcpPayload = ModbusTcpPayload(
            modbusPayload.transactionId,
            modbusPayload.unitId,
            responsePdu
        )

        return ModbusResponse(modbusTcpPayload)
    }
}

