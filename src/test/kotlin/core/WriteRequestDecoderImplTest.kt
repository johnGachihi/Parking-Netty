package core

import com.digitalpetri.modbus.codec.ModbusTcpPayload
import com.digitalpetri.modbus.requests.WriteMultipleRegistersRequest
import core.decode.WriteDataDecoder
import core.decode.WriteDataDecoderRegistry
import core.decode.WriteRequestDecoderImpl
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class WriteRequestDecoderImplTest {

    @Test
    fun `When supported action provided, then uses corresponding decoder`() {
        val registry = WriteDataDecoderRegistry()
        val writeDataDecoder = MockWriteDataDecoder()
        registry.register(RequestAction.Entry, writeDataDecoder)

        val modbusPayload = makeModbusTcpPayload(RequestAction.Entry)

        WriteRequestDecoderImpl(registry).decode(modbusPayload)

        assertEquals(writeDataDecoder.callCount, 1)
    }

    @Test
    fun `When action is unsupported, then throws UnsupportedActionException`() {
        assertThrows(UnsupportedActionException::class.java) {
            WriteRequestDecoderImpl(WriteDataDecoderRegistry()).decode(makeModbusTcpPayload())
        }
    }

    class MockWriteDataDecoder : WriteDataDecoder {
        var callCount = 0
        override fun decode(byteBuf: ByteBuf): Any {
            callCount++
            return "Something, I don't care"
        }
    }

    private fun makeModbusTcpPayload(action: RequestAction = RequestAction.Entry): ModbusTcpPayload {
        return ModbusTcpPayload(1, 1, WriteMultipleRegistersRequest(
            action.code, 1, Unpooled.EMPTY_BUFFER
        ))
    }
}