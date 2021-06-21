package app.endpoints

import app.decoders.RfidDecoder
import router.modbus.WriteRequestEndpoint
import router.modbus.Decoder

class RfidEndpoint : WriteRequestEndpoint<Long>() {
    override fun handleRequest(data: Long) {
        println(data)
    }

    override fun createDecoder(): Decoder<Long> {
        return RfidDecoder()
    }
}