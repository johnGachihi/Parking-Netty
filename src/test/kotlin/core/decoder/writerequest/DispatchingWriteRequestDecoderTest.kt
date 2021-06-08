package core.decoder.writerequest

import com.digitalpetri.modbus.codec.ModbusTcpPayload
import com.digitalpetri.modbus.requests.WriteMultipleRegistersRequest
import core.UnsupportedActionException
import core.WriteRequest
import io.netty.buffer.Unpooled
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import core.RequestAction

internal class DispatchingWriteRequestDecoderTest {
    @Test
    fun `When valid action code provided, then uses appropriate WriteRequestDecoder`() {
        val exitRequestDecoder = MockWriteRequestDecoder()
        val entryRequestDecoder = MockWriteRequestDecoder()

        val dispatchingWriteRequestDecoder = DispatchingWriteRequestDecoder(
            mapOf(
                RequestAction.Exit to exitRequestDecoder,
                RequestAction.Entry to entryRequestDecoder
            ))

        val modbusTcpPayload = ModbusTcpPayload(1, 1,
            WriteMultipleRegistersRequest(RequestAction.Exit.code, 1, Unpooled.EMPTY_BUFFER))
        dispatchingWriteRequestDecoder.decode(modbusTcpPayload)

        assertEquals(exitRequestDecoder.callCount, 1)
        assertEquals(entryRequestDecoder.callCount, 0)
    }

    @Test
    fun `When action code is not in actionToDecoder map, then throw Unsupported Action`() {
        val dispatchingWriteRequestDecoder = DispatchingWriteRequestDecoder(emptyMap())

        val modbusTcpPayload = ModbusTcpPayload(1, 1,
            WriteMultipleRegistersRequest(RequestAction.Exit.code, 1, Unpooled.EMPTY_BUFFER))

        assertThrows(UnsupportedActionException::class.java) {
            dispatchingWriteRequestDecoder.decode(modbusTcpPayload)
        }
    }

    class MockWriteRequestDecoder : WriteRequestDecoder {
        var callCount = 0

        override fun decode(modbusTcpPayload: ModbusTcpPayload): WriteRequest {
            callCount++
            return WriteRequest(modbusTcpPayload.modbusPdu as WriteMultipleRegistersRequest)
        }
    }
}