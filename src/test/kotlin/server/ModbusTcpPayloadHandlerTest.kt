package server

import com.digitalpetri.modbus.ExceptionCode
import com.digitalpetri.modbus.FunctionCode
import com.digitalpetri.modbus.codec.ModbusTcpPayload
import com.digitalpetri.modbus.requests.WriteMultipleRegistersRequest
import com.digitalpetri.modbus.responses.ExceptionResponse
import core.WriteRequest
import core.decoder.writerequest.WriteRequestDecoder
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.embedded.EmbeddedChannel
import io.netty.handler.codec.FixedLengthFrameDecoder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ModbusTcpPayloadHandlerTest {
    @Nested
    inner class InboundTest {
        @Test
        @DisplayName("When inbound data is not instance of ModbusTcpPayload, " +
                "then does not pass inbound data nor write anything")
        fun `test when inbound data not a ModbusTcpPayload`() {
            val channel = EmbeddedChannel(ModbusTcpPayloadHandler(MockWriteRequestDecoder()))

            channel.writeInbound("Not ModbusTcpPayload")
            channel.finish()

            assertNull(channel.readInbound())
            assertNull(channel.readOutbound())
        }

        @Test
        fun `When modbus function-code is WriteMultipleRegister, then passes one inbound WriteRequest`() {
            val input = ModbusTcpPayload(1, 1,
                WriteMultipleRegistersRequest(1, 1, Unpooled.EMPTY_BUFFER))

            val channel = EmbeddedChannel(ModbusTcpPayloadHandler(MockWriteRequestDecoder()))
            channel.writeInbound(input)
            channel.finish()

            assertNotNull(channel.readInbound<WriteRequest>())
            assertNull(channel.readInbound())
        }

        @Test
        fun `test calls WriteRequestDecoder's decode()`() {
            val input = ModbusTcpPayload(1, 1,
                WriteMultipleRegistersRequest(1, 1, Unpooled.EMPTY_BUFFER))

            val writeRequestDecoder = MockWriteRequestDecoder()

            val channel = EmbeddedChannel(ModbusTcpPayloadHandler(writeRequestDecoder))
            channel.writeInbound(input)
            channel.finish()

            assertEquals(writeRequestDecoder.callCount, 1)
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
                val input = ModbusTcpPayload(1, 1) { it }

                val channel = EmbeddedChannel(ModbusTcpPayloadHandler(MockWriteRequestDecoder()))
                channel.writeInbound(input)
                channel.finish()

                val written = channel.readOutbound<ModbusTcpPayload>()
                val exceptionResponse = written.modbusPdu as ExceptionResponse

                assertEquals(exceptionResponse.exceptionCode, ExceptionCode.IllegalFunction)
            }
        }

        inner class MockWriteRequestDecoder : WriteRequestDecoder {
            var callCount = 0
            override fun decode(modbusTcpPayload: ModbusTcpPayload): WriteRequest {
                callCount++
                val pdu = modbusTcpPayload.modbusPdu as WriteMultipleRegistersRequest
                return WriteRequest(pdu)
            }
        }


/*
        @Test
        @DisplayName("When modbus function-code is WriteMultipleRegister, " +
                "then adds WriteRequestHandler to pipeline after ModbusTcpPayloadHandler")
        fun `test adds WriteRequestHandler`() {
            val input = ModbusTcpPayload(1, 1,
                WriteMultipleRegistersRequest(1, 1, Unpooled.buffer()))

            val channel = EmbeddedChannel()
            channel.pipeline().addLast("modbusTcpPayloadHandler", ModbusTcpPayloadHandler())

            channel.writeInbound(input)

            val (first, second) = channel.pipeline().names()
            assertEquals(first, "modbusTcpPayloadHandler")
            assertEquals(second, "writeRequestHandler")
        }
*/
    }


    @Test
    fun testInputDecoded() {
        val buf = Unpooled.buffer()
        for (i in 1..6)
            buf.writeByte(i)

        val input = buf.duplicate()
        val channel = EmbeddedChannel(FixedLengthFrameDecoder(2))
        channel.writeInbound(input)
        channel.finish()

        var read = channel.readInbound<ByteBuf>()
        assertEquals(buf.readSlice(2), read)
        read.release()

        read = channel.readInbound()
        assertEquals(buf.readSlice(2), read)
        read.release()

        read = channel.readInbound()
        assertEquals(buf.readSlice(2), read)
        read.release()

        read = channel.readInbound()
        assertNull(read)
    }

    @Test
    fun test2() {
        val buf = Unpooled.buffer()
        for (i in 1..6)
            buf.writeByte(i)

        val input = buf.duplicate()
        val channel = EmbeddedChannel(FixedLengthFrameDecoder(3))
        channel.writeInbound(input.readBytes(2))
        assertNull(channel.readInbound())

        channel.writeInbound(input.readBytes(4))
        var read = channel.readInbound<ByteBuf>()
        assertEquals(buf.readSlice(3), read)
        read.release()

        read = channel.readInbound()
        assertEquals(buf.readSlice(3), read)
        read.release()
    }

    @Test
    fun test3() {
        val a = Unpooled.buffer()
        a.writeByte(1)

        val b = Unpooled.buffer()
        b.writeByte(2)

        assertNotEquals(a, b)
    }
}