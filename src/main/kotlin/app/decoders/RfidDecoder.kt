package app.decoders

import io.netty.buffer.ByteBuf
import io.netty.handler.codec.DecoderException
import modbus.endpoints.Decoder

class RfidDecoder : Decoder<Long> {
    override fun decode(byteBuf: ByteBuf): Long {
        if (byteBuf.readableBytes() != 8) {
            throw DecoderException()
        }
        return byteBuf.readLongLE()
    }
}