package app.endpoints

import app.IllegalDataException
import app.services.EntryService
import com.digitalpetri.modbus.ExceptionCode
import io.mockk.every
import io.mockk.verify
import io.netty.buffer.Unpooled
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.test.mock.declareMock

class EntryEndpointTest : EndpointTest() {
    private lateinit var entryService: EntryService

    @BeforeEach
    fun init() {
        entryService = declareMock()
    }

    @Test
    fun `Listens to action code 1 requests`() {
        val data = Unpooled.buffer().writeLongLE(123)
        val response = sendModbusWriteRequest(1, data)

        response.assertOk()
    }

    @Test
    fun `Accepts only a long ByteBuf`() {
        val data = Unpooled.buffer().writeInt(123)
        sendModbusWriteRequest(1, data)
            .assertExceptional(ExceptionCode.IllegalDataValue)
    }

    @Test
    fun `service layer logic called appropriately`() {
        val entryService = declareMock<EntryService>()

        val data = Unpooled.buffer().writeLongLE(123)
        sendModbusWriteRequest(1, data)
            .assertOk()

        verify { entryService.addVisit(123) }
    }

    @Test
    fun `When entry attempt throws IllegalDataException, then returns IllegalDataValue exception response`() {
        val entryService = declareMock<EntryService>()
        every { entryService.addVisit(any()) } throws IllegalDataException()

        val data = Unpooled.buffer().writeLongLE(123)
        sendModbusWriteRequest(1, data)
            .assertExceptional(ExceptionCode.IllegalDataValue)
    }
}

