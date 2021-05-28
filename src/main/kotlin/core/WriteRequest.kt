package core

import io.netty.buffer.ByteBuf

data class WriteRequest(val data: ByteBuf) : Request {
}