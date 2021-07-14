package app.endpoints

import app.decoders.RfidDecoder
import app.services.ExitService
import router.modbus.Decoder
import router.modbus.WriteRequestEndpoint

class ExitEndpoint(
    private val exitService: ExitService
) : WriteRequestEndpoint<Long>() {
    override fun handleRequest(data: Long) {
        exitService.finishVisit(data)
    }

    override fun createDecoder(): Decoder<Long> = RfidDecoder()
}