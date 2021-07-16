package modbus.exceptionhandling

import com.digitalpetri.modbus.ExceptionCode
import com.digitalpetri.modbus.codec.ModbusTcpPayload
import com.digitalpetri.modbus.responses.ExceptionResponse
import core.Request
import core.Response
import modbus.ModbusRequest
import modbus.ModbusResponse
import core.exceptionhandling.ExceptionHandlingStrategy
import core.exceptionhandling.ResponseException
import core.exceptionhandling.ResponseExceptionStatus
import io.netty.handler.codec.DecoderException

class ModbusTcpExceptionHandlingStrategy : ExceptionHandlingStrategy {
    override fun handleException(exception: Exception, request: Request): Response {
        request as ModbusRequest

        val exceptionPdu = ExceptionResponse(
            request.modbusTcpPayload.modbusPdu.functionCode,
            getExceptionCode(exception)
        )
        val modbusTcpPayload = ModbusTcpPayload(
            request.modbusTcpPayload.transactionId,
            request.modbusTcpPayload.unitId,
            exceptionPdu
        )

        return ModbusResponse(modbusTcpPayload)
    }

    private fun getExceptionCode(exception: Exception): ExceptionCode {
        if (exception is ResponseException) {
            if (exception.status == ResponseExceptionStatus.INVALID_DATA) {
                return ExceptionCode.IllegalDataValue
            }
        } else if (exception is DecoderException) { // TODO: DecoderException is a Netty dependency
            return ExceptionCode.IllegalDataValue
        }
        return ExceptionCode.SlaveDeviceFailure
    }
}