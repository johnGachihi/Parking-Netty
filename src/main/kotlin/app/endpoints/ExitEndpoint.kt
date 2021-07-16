package app.endpoints

import app.IllegalDataException
import app.UnservicedFeeException
import app.decoders.RfidDecoder
import app.services.ExitService
import exceptionhandling.ResponseException
import exceptionhandling.ResponseExceptionStatus
import router.modbus.Decoder
import router.modbus.WriteRequestEndpoint

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