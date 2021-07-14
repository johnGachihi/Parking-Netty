package app.endpoints

import app.IllegalDataException
import app.UnservicedFeeException
import app.services.ExitService
import com.digitalpetri.modbus.ExceptionCode
import io.mockk.every
import io.mockk.verify
import io.netty.buffer.Unpooled
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.koin.test.mock.declareMock

@DisplayName("Test ExitEndpoint")
internal class ExitEndpointTest : EndpointTest() {
    private lateinit var exitService: ExitService

    @BeforeEach
    fun init() {
        exitService = declareMock()
    }

    @Test
    fun `Listens to actionCode 2 requests`() {
        sendModbusWriteRequest(2, Unpooled.buffer().writeLongLE(123))
            .assertOk()
    }

    @Test
    fun `Uses ExitService to attempt to finish a visit`() {
        sendModbusWriteRequest(2, Unpooled.buffer().writeLongLE(1234567))
            .assertOk()

        verify { exitService.finishVisit(1234567) }
    }

    @Test
    fun `When attempt to exit throws an IllegalDataException, then returns IllegalDataValue modbus exception response`() {
        every { exitService.finishVisit(any()) } throws IllegalDataException()

        sendModbusWriteRequest(2, Unpooled.buffer().writeLongLE(123))
            .assertExceptional(ExceptionCode.IllegalDataValue)
    }

    @Test
    fun `When attempt to exit throws an UnservicedFeeException, then returns UnservicedFeeException modbus exception response`() {
        every { exitService.finishVisit(any()) } throws UnservicedFeeException()

        sendModbusWriteRequest(2, Unpooled.buffer().writeLongLE(123))
            .assertExceptional(ExceptionCode.IllegalDataValue)
    }
}