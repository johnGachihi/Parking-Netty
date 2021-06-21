package router.modbus

import io.netty.buffer.ByteBuf

interface Decoder<out R> {
    fun decode(byteBuf: ByteBuf): R
}