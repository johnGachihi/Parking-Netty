package intercepting

import core.Request
import core.Response
import createMockRequest
import createMockResponse
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class InterceptorManagerImplTest {
    @Test
    fun `interceptRequest, when interceptor-chain is empty, returns request as was provided`() {
        val interceptorManager = InterceptorManagerImpl(listOf())

        val expectedRequest = createMockRequest()
        val actualRequest = interceptorManager.interceptRequest(expectedRequest)

        assertEquals(expectedRequest, actualRequest)
    }

    @Test
    fun `interceptResponse, when interceptor-chain is empty, returns response as was provided`() {
        val interceptorManager = InterceptorManagerImpl(listOf())

        val expectedResponse = createMockResponse()
        val actualResponse = interceptorManager.interceptResponse(expectedResponse)

        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `when interceptor provided, then appropriate methods called`() {
        val interceptorOne = mockk<Interceptor>(relaxed = true)
        val interceptorTwo = mockk<Interceptor>(relaxed = true)
        val interceptorManager = InterceptorManagerImpl(listOf(
            interceptorOne, interceptorTwo))

        // Request
        interceptorManager.interceptRequest(mockk())
        verify(exactly = 1) { interceptorOne.interceptRequest(any()) }
        verify(exactly = 1) { interceptorTwo.interceptRequest(any()) }

        // Response
        interceptorManager.interceptResponse(mockk())
        verify(exactly = 1) { interceptorOne.interceptResponse(any()) }
        verify(exactly = 1) { interceptorTwo.interceptResponse(any()) }
    }

    @Test
    fun `Passes result of previous interceptor to next interceptor`() {
        val interceptorOne = mockk<Interceptor>(relaxed = true)
        val interceptorTwo = mockk<Interceptor>(relaxed = true)
        val interceptorManager = InterceptorManagerImpl(listOf(
            interceptorOne, interceptorTwo
        ))

        // Request
        val request = mockk<Request>()
        every { interceptorOne.interceptRequest(any()) } returns request

        interceptorManager.interceptRequest(mockk(relaxed = true))

        verify { interceptorTwo.interceptRequest(request) }


        //Response
        val response = mockk<Response>()
        every { interceptorOne.interceptResponse(any()) } returns response

        interceptorManager.interceptResponse(mockk(relaxed = true))

        verify { interceptorTwo.interceptResponse(response) }
    }

    @Test
    fun `Returns result of last interceptor`() {
        val interceptor: Interceptor = mockk()
        val interceptorManager = InterceptorManagerImpl(listOf(interceptor))

        //Request
        val expectedRequest: Request = mockk()
        every { interceptor.interceptRequest(any()) } returns expectedRequest

        val actualRequest = interceptorManager.interceptRequest(mockk())

        assertEquals(expectedRequest, actualRequest)


        // Response
        val expectedResponse: Response = mockk()
        every { interceptor.interceptResponse(any()) } returns expectedResponse

        val actualResponse = interceptorManager.interceptResponse(mockk())

        assertEquals(expectedResponse, actualResponse)
    }
}

