package router

import core.Request
import core.Response
import createMockRequest
import intercepting.InterceptorManager
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class RequestHandlerImplTest {
    @RelaxedMockK
    lateinit var interceptorManager: InterceptorManager

    @RelaxedMockK
    lateinit var endpointFactory: EndpointFactory

    @RelaxedMockK
    lateinit var exceptionHandler: ExceptionHandler

    @InjectMockKs
    lateinit var requestHandler: RequestHandlerImpl

    @Test
    fun `handleRequest calls InterceptorManager#interceptRequest once`() {
        requestHandler.handleRequest(createMockRequest())

        verify(exactly = 1) {
            interceptorManager.interceptRequest(any())
        }
    }

    @Test
    fun `handleRequest calls InterceptorManager#interceptRequest with the Request argument it receives`() {
        val request = mockk<Request>(relaxed = true)
        requestHandler.handleRequest(request)

        verify {
            interceptorManager.interceptRequest(request)
        }
    }

    @Test
    fun `handleRequest calls EndpointFactory#getEndpoint once`() {
        requestHandler.handleRequest(createMockRequest())

        verify(exactly = 1) {
            endpointFactory.getEndpoint(any())
        }
    }

    @Test
    fun `handleRequest calls EndpointFactory#getEndpoint with the action-code from the request it received`() {
        val expectedActionCode = 123

        requestHandler.handleRequest(
            createMockRequest(withActionCode = expectedActionCode))

        verify {
            endpointFactory.getEndpoint(expectedActionCode)
        }
    }

    @Test
    fun `handleRequest calls Endpoint returned by EndpointFactory#getEndpoint once`() {
        val endpoint = mockk<Endpoint>(relaxed = true)
        every { endpointFactory.getEndpoint(any()) } returns endpoint

        requestHandler.handleRequest(createMockRequest())

        verify(exactly = 1) { endpoint.handleRequest(any()) }
    }

    @Test
    fun `handleRequest calls Endpoint#handleRequest with result of InterceptorManager#interceptRequest`() {
        val endpoint = mockk<Endpoint>(relaxed = true)
        every { endpointFactory.getEndpoint(any()) } returns endpoint
        val request = createMockRequest()
        every { interceptorManager.interceptRequest(any()) } returns request

        requestHandler.handleRequest(createMockRequest())

        verify { endpoint.handleRequest(request) }
    }

    @Test
    fun `handleRequest calls InterceptorManager#interceptResponse once`() {
        requestHandler.handleRequest(createMockRequest())

        verify(exactly = 1) {
            interceptorManager.interceptResponse(any())
        }
    }

    @Test
    fun `handleRequest calls InterceptorManager#interceptResponse, with result of Endpoint#handleRequest`() {
        val endpoint = mockk<Endpoint>()
        val response = mockk<Response>()
        every { endpoint.handleRequest(any()) } returns response
        every { endpointFactory.getEndpoint(any()) } returns endpoint

        requestHandler.handleRequest(mockk(relaxed = true))

        verify { interceptorManager.interceptResponse(response) }
    }

    @Test
    fun `handleRequest returns result of InterceptorManager#interceptResponse`() {
        val expectedResponse = mockk<Response>()
        every { interceptorManager.interceptResponse(any()) } returns expectedResponse

        val actualResponse = requestHandler.handleRequest(mockk(relaxed = true))

        assertEquals(expectedResponse, actualResponse)
    }
}