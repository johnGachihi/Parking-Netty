package app.decoders

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.handler.codec.DecoderException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class RfidDecoderTest {
    @Test
    fun `When data is not 8 bytes long, then throws DecoderException`() {
        assertThrows(DecoderException::class.java) {
            RfidDecoder().decode(makeByteBuf(2))
        }

        assertThrows(DecoderException::class.java) {
            RfidDecoder().decode(makeByteBuf(10))
        }
    }

    @Test
    fun `When data is 8 bytes long, then returns long from ByteBuf content`() {
        val data = make8Byte_ByteBufFromLong(10)

        assertEquals(10L, RfidDecoder().decode(data))
    }

    private fun makeByteBuf(n: Int): ByteBuf {
        val data = Unpooled.buffer()
        for (i in 1..n) {
            data.writeByte(i)
        }
        return data
    }

    private fun make8Byte_ByteBufFromLong(l: Long): ByteBuf =
        Unpooled.buffer().writeLongLE(l)
}