package exceptionhandling.modbus

import com.digitalpetri.modbus.ExceptionCode
import com.digitalpetri.modbus.codec.ModbusTcpPayload
import com.digitalpetri.modbus.responses.ExceptionResponse
import core.Request
import core.Response
import core.modbus.ModbusRequest
import core.modbus.ModbusResponse
import exceptionhandling.ExceptionHandlingStrategy
import exceptionhandling.ResponseException
import exceptionhandling.ResponseExceptionStatus
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