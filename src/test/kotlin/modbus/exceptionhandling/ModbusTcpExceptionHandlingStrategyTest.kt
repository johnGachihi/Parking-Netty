package modbus.exceptionhandling

import com.digitalpetri.modbus.ExceptionCode
import com.digitalpetri.modbus.FunctionCode
import com.digitalpetri.modbus.codec.ModbusTcpPayload
import com.digitalpetri.modbus.requests.WriteMultipleRegistersRequest
import com.digitalpetri.modbus.responses.ExceptionResponse
import core.Request
import core.Response
import modbus.ModbusResponse
import modbus.ModbusWriteRequest
import core.exceptionhandling.ResponseException
import core.exceptionhandling.ResponseExceptionStatus
import io.netty.buffer.Unpooled
import io.netty.handler.codec.DecoderException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertIs

@DisplayName("Test ModbusTcpExceptionHandlingStrategy")
internal class ModbusTcpExceptionHandlingStrategyTest {
    private lateinit var modbusTcpExceptionHandlingStrategy: ModbusTcpExceptionHandlingStrategy
    private lateinit var response: Response

    @BeforeEach
    fun init() {
        modbusTcpExceptionHandlingStrategy = ModbusTcpExceptionHandlingStrategy()
    }

//    Check everything but exception code, the constant things
    @Test
    fun `Forms the ModbusResponse appropriately from the Request`() {
        val response = modbusTcpExceptionHandlingStrategy.handleException(
            Exception(),
            makeModbusWriteRequest(1, 1)
        )

        assertIs<ModbusResponse>(response)
        assertEquals(1, response.modbusTcpPayload.transactionId, "Transaction Id")
        assertEquals(1, response.modbusTcpPayload.unitId, "Unit Id")
        assertIs<ExceptionResponse>(response.modbusTcpPayload.modbusPdu, "PDU type")
        assertEquals(FunctionCode.WriteMultipleRegisters, (response.modbusTcpPayload.modbusPdu as ExceptionResponse).functionCode)
    }

//    Starting here tests check only exception code.
    @Nested
    @DisplayName("When exception thrown is a ResponseException")
    inner class TestWhenExceptionIsAResponseException {
        @Test
        fun `and status is INVALID_DATA, then returns ILLEGALDATAVALUE modbus exceptional response`() {
            runExceptionHandlingStrategy(
                ResponseException(ResponseExceptionStatus.INVALID_DATA, "")
            )

            assertExceptionCodeIs(ExceptionCode.IllegalDataValue)
        }
    }

    @Test
    fun `When exception is a DecoderException, then returns ILLEGALDATAVALUE modbus exceptional response`() {
        runExceptionHandlingStrategy(
            DecoderException()
        )

        assertExceptionCodeIs(ExceptionCode.IllegalDataValue)
    }

    @Test
    fun `When exception is not any of the above, then returns a SLAVEDEVICEFAILURE modbus exceptional response`() {
        runExceptionHandlingStrategy(Exception())

        assertExceptionCodeIs(ExceptionCode.SlaveDeviceFailure)
    }


    private fun makeModbusWriteRequest(transactionId: Short = 1, unitId: Short = 1): Request {
        val writeMultipleRegistersRequest = WriteMultipleRegistersRequest(
            1, 1, Unpooled.EMPTY_BUFFER
        )
        val modbusTcpPayload = ModbusTcpPayload(
            transactionId, unitId, writeMultipleRegistersRequest
        )
        return ModbusWriteRequest(1, modbusTcpPayload)
    }

    private fun runExceptionHandlingStrategy(
        exception: Exception,
        request: Request = makeModbusWriteRequest()
    ) {
        response = modbusTcpExceptionHandlingStrategy.handleException(
            exception, request
        )
    }

    private fun assertExceptionCodeIs(exceptionCode: ExceptionCode) {
        assertIs<ModbusResponse>(response)

        val exceptionPdu = (response as ModbusResponse).modbusTcpPayload.modbusPdu as ExceptionResponse
        assertEquals(exceptionCode, exceptionPdu.exceptionCode)
    }
}
