package core

import core.decode.WriteDataDecoder
import core.decode.WriteDataDecoderRegistry
import io.netty.buffer.ByteBuf
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class WriteDataDecoderRegistryTest {

    @Test
    fun `When registry does not contain any decoders, then getWriteDataDecoder returns null`() {
        assertNull(WriteDataDecoderRegistry().getDecoder(RequestAction.Entry))
    }

    @Test
    fun `When decoder registered, then getWriteDataDecoder returns appropriate WriteDataDecoder`() {
        val registry = WriteDataDecoderRegistry()
        val expected = MockWriteDataDecoder()
        registry.register(RequestAction.Entry, expected)

        val actual = registry.getDecoder(RequestAction.Entry)
        assertEquals(actual, expected)
    }

    class MockWriteDataDecoder : WriteDataDecoder {
        override fun decode(byteBuf: ByteBuf): Any {
            return "something"
        }
    }
}