package app.endpoints

import app.IllegalDataException
import app.UnservicedFeeException
import app.decoders.RfidDecoder
import app.services.ExitService
import core.exceptionhandling.ResponseException
import core.exceptionhandling.ResponseExceptionStatus
import modbus.endpoints.Decoder
import modbus.endpoints.WriteRequestEndpoint

class ExitEndpoint(
    private val exitService: ExitService
) : WriteRequestEndpoint<Long>() {
    override fun handleRequest(data: Long) {
        try {
            exitService.finishVisit(data)
        } catch (e: IllegalDataException) {
            throw ResponseException(ResponseExceptionStatus.INVALID_DATA, "")
        } catch (e: UnservicedFeeException) {
            throw ResponseException(ResponseExceptionStatus.INVALID_DATA, "")
        }
    }

    override fun createDecoder(): Decoder<Long> = RfidDecoder()
}