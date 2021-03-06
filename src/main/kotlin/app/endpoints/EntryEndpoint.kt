package app.endpoints

import app.IllegalDataException
import app.decoders.RfidDecoder
import app.services.EntryService
import core.exceptionhandling.ResponseException
import core.exceptionhandling.ResponseExceptionStatus
import modbus.endpoints.Decoder
import modbus.endpoints.WriteRequestEndpoint

class EntryEndpoint(
    private val entryService: EntryService
) : WriteRequestEndpoint<Long>() {
    override fun handleRequest(data: Long) {
        try {
            entryService.addVisit(data)
        } catch (exc: IllegalDataException) {
            throw ResponseException(ResponseExceptionStatus.INVALID_DATA, "")
        }
    }

    override fun createDecoder(): Decoder<Long> = RfidDecoder()
}