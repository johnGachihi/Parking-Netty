package exceptionhandling

import core.Request
import core.exceptionhandling.ExceptionHandlerImpl
import core.exceptionhandling.ExceptionHandlingStrategy
import createMockResponse
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Test ExceptionHandlerImpl")
internal class ExceptionHandlerImplTest {
    @Test
    fun `Calls the ExceptionHandlingStrategy defined in Request`() {
        val exceptionHandlingStrategy = mockk<ExceptionHandlingStrategy>(relaxed = true)
        val mockRequest = MockRequest(exceptionHandlingStrategy)

        val exception = Exception()
        ExceptionHandlerImpl().handleException(exception, mockRequest)

        verify { exceptionHandlingStrategy.handleException(exception, mockRequest) }
    }

    @Test
    fun `Returns what the ExceptionHandlingStrategy it calls returns`() {
        val exceptionHandlingStrategy = mockk<ExceptionHandlingStrategy>(relaxed = true)
        val expectedResponse = createMockResponse()
        every {
            exceptionHandlingStrategy.handleException(any(), any())
        } returns expectedResponse

        val actualResponse = ExceptionHandlerImpl().handleException(Exception(), MockRequest(exceptionHandlingStrategy))

        assertEquals(expectedResponse, actualResponse)
    }

    class MockRequest(
        override val exceptionHandlingStrategy: ExceptionHandlingStrategy
    ) : Request {
        override val actionCode: Int = 1
    }
}