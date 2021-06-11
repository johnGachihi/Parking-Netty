package core.decode

import io.netty.buffer.ByteBuf

interface WriteDataDecoder {
    fun decode(byteBuf: ByteBuf): Any
}