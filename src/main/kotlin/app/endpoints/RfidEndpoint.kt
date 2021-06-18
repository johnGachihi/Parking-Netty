package app.endpoints

import app.decoders.RfidDecoder
import modbus.WriteRequestEndpoint
import modbus.core.Decoder

class RfidEndpoint : WriteRequestEndpoint<Long>() {
    override fun handleRequest(data: Long) {
        println(data)
    }

    override fun createDecoder(): Decoder<Long> {
        return RfidDecoder()
    }
}