package app.endpoints

import app.decoders.RfidDecoder
import app.services.EntryService
import router.modbus.Decoder
import router.modbus.WriteRequestEndpoint

class EntryEndpoint(
    private val entryService: EntryService
) : WriteRequestEndpoint<Long>() {
    override fun handleRequest(data: Long) {
        entryService.addVisit(data)
    }

    override fun createDecoder(): Decoder<Long> = RfidDecoder()
}