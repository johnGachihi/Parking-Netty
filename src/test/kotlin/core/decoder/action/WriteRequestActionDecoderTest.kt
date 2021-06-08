package core.decoder.action

import com.digitalpetri.modbus.codec.ModbusTcpPayload
import com.digitalpetri.modbus.requests.WriteMultipleRegistersRequest
import io.netty.buffer.Unpooled
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import core.RequestAction

internal class WriteRequestActionDecoderTest {
    // Use loop to test each RequestAction
    @Test
    fun `When action code recognized, then returns appropriate RequestAction`() {
        val payload = ModbusTcpPayload(1, 1,
            WriteMultipleRegistersRequest(RequestAction.Exit.code, 1, Unpooled.EMPTY_BUFFER))
        val action = WriteRequestActionDecoder().getAction(payload)

        assertEquals(action, RequestAction.Exit)
    }
}