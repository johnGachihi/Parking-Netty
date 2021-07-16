package app.endpoints

import app.decoders.RfidDecoder
import modbus.endpoints.WriteRequestEndpoint
import modbus.endpoints.Decoder

class RfidEndpoint : WriteRequestEndpoint<Long>() {
    override fun handleRequest(data: Long) {
        println(data)
    }

    override fun createDecoder(): Decoder<Long> {
        return RfidDecoder()
    }
}