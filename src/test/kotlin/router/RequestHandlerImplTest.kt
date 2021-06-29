package router

import core.Request
import core.Response
import createMockRequest
import db.HibernateSessionContextManager
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
    lateinit var hibernateSessionContextManager: HibernateSessionContextManager

    @RelaxedMockK
    lateinit var endpointFactory: EndpointFactory

    @RelaxedMockK
    lateinit var exceptionHandler: ExceptionHandler

    @InjectMockKs
    lateinit var requestHandler: RequestHandlerImpl

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
}