package server

import com.digitalpetri.modbus.ExceptionCode
import com.digitalpetri.modbus.FunctionCode
import com.digitalpetri.modbus.codec.ModbusTcpPayload
import com.digitalpetri.modbus.responses.ExceptionResponse
import core.RequestAction
import core.WriteRequest
import core.decode.WriteRequestDecoder
import io.netty.channel.embedded.EmbeddedChannel
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ModbusTcpPayloadHandlerTest {
    @Nested
    inner class InboundTest {
        lateinit var channel: EmbeddedChannel
        private lateinit var writeRequestDecoder: MockWriteRequestDecoder

        @BeforeEach
        fun initChannelAndWriteRequestDecoder() {
            writeRequestDecoder = MockWriteRequestDecoder()
            channel = EmbeddedChannel(
                ModbusTcpPayloadHandler(writeRequestDecoder))
        }

        @Test
        @DisplayName("When inbound data is not instance of ModbusTcpPayload, then does nothing")
        fun `test when inbound data not a ModbusTcpPayload`() {
            writeInboundAndFinish("Not ModbusTcpPayload")

            assertDecodeCalledTimes(0)
            assertNull(channel.readInbound())
            assertNull(channel.readOutbound())
        }

        @Test
        fun `When modbus function-code is WriteMultipleRegister, then passes one inbound WriteRequest`() {
            val input = makeModbusPayload(FunctionCode.WriteMultipleRegisters)

            writeInboundAndFinish(input)

            assertNotNull(channel.readInbound<WriteRequest>())
            assertNull(channel.readInbound())
        }

        @Test
        fun `test calls WriteRequestDecoder's decode()`() {
            val input = makeModbusPayload(FunctionCode.WriteMultipleRegisters)

            writeInboundAndFinish(input)

            assertDecodeCalledTimes(1)
        }

        @Test
        fun `When modbus function-code is any other, then IllegalFunction exception is written`() {
            val allowedFunctionCodes = listOf(
                FunctionCode.WriteMultipleRegisters
            )
            val otherFunctionCodes = FunctionCode.values()
                .toList()
                .minus(allowedFunctionCodes)

            otherFunctionCodes.forEach {
                val input = makeModbusPayload(it)

                writeInboundAndFinish(input)

                assertWritesModbusIllegalFunctionException()

                // For next iteration
                initChannelAndWriteRequestDecoder()
            }
        }

        inner class MockWriteRequestDecoder : WriteRequestDecoder {
            var callCount = 0
            override fun decode(modbusTcpPayload: ModbusTcpPayload): WriteRequest {
                callCount++
                return WriteRequest(
                    RequestAction.fromCode(-1),
                    "Any data, I don't care.",
                    modbusTcpPayload
                )
            }
        }

        private fun writeInboundAndFinish(input: Any) {
            channel.writeInbound(input)
            channel.finish()
        }

        private fun makeModbusPayload(functionCode: FunctionCode): ModbusTcpPayload {
            val anyNumber: Short = 1
            return ModbusTcpPayload(anyNumber, anyNumber) { functionCode }
        }

        private fun assertDecodeCalledTimes(n: Int) {
            assertEquals(n, writeRequestDecoder.callCount)
        }

        private fun assertWritesModbusIllegalFunctionException() {
            val written = channel.readOutbound<ModbusTcpPayload>()
            val exceptionResponse = written.modbusPdu as ExceptionResponse

            assertEquals(exceptionResponse.exceptionCode, ExceptionCode.IllegalFunction)
        }
    }
}
