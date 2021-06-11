package core.decode

import io.netty.buffer.ByteBuf
import io.netty.handler.codec.DecoderException

class RfidDecoder : WriteDataDecoder {
    override fun decode(byteBuf: ByteBuf): Long {
        if (byteBuf.readableBytes() != 8) {
            throw DecoderException()
        }
        return byteBuf.readLongLE()
    }
}