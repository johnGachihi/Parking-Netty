package router

import core.Response
import createMockRequest
import createMockResponse
import db.HibernateSessionContextManager
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
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
    fun `Uses EndpointFactory to get Endpoint using the received Request's action-code`() {
        val actionCode = 192
        val request = createMockRequest(withActionCode = actionCode)

        requestHandler.handleRequest(request)

        verify(exactly = 1) { endpointFactory.getEndpoint(actionCode) }
    }

    @Test
    fun `Uses the Endpoint returned by EndpointFactory to handle the received Request`() {
        val endpoint = mockk<Endpoint>(relaxed = true)
        every { endpointFactory.getEndpoint(any()) } returns endpoint

        val request = createMockRequest()
        requestHandler.handleRequest(request)

        verify(exactly = 1) { endpoint.handleRequest(request) }
    }

    @Test
    fun `When exception is thrown when handling a Request, then ExceptionHandler is passed the exception thrown and Request being handled`() {
        val thrownException = Exception()
        makeRequestHandlingThrowException(exception = thrownException)

        val requestBeingHandled = createMockRequest()
        requestHandler.handleRequest(requestBeingHandled)

        verify(exactly = 1) { exceptionHandler.handleException(thrownException, requestBeingHandled) }
    }

    @Test
    fun `When exception is thrown when handling a Request, the ExceptionHandler is used to create the returned Response`() {
        makeRequestHandlingThrowException()

        val responseFromExceptionHandler = createMockResponse()
        makeExceptionHandlerReturn(responseFromExceptionHandler)

        assertRequestHandlerReturns(responseFromExceptionHandler)
    }

    @Test
    fun `When Request is handled successfully, then the used Endpoint's Response is returned`() {
        val endpoint = mockk<Endpoint>(relaxed = true)
        val responseFromEndpoint = createMockResponse()
        every { endpoint.handleRequest(any()) } returns responseFromEndpoint

        every { endpointFactory.getEndpoint(any()) } returns endpoint

        assertRequestHandlerReturns(responseFromEndpoint)
    }

    @Test
    fun `Begins hibernate session context, before attempting to handling request`() {
        requestHandler.handleRequest(createMockRequest())

        verifyOrder {
            hibernateSessionContextManager.beginSessionContext()
            endpointFactory.getEndpoint(any())
        }
    }

    @Test
    fun `Closes hibernate session context, after Request is handled`() {
        requestHandler.handleRequest(createMockRequest())

        verifyOrder {
            endpointFactory.getEndpoint(any())
            hibernateSessionContextManager.closeSessionContext()
        }
    }

    @Test
    fun `Closes hibernate session context exceptionally, if an exception is thrown when a Request is being handled`() {
        makeRequestHandlingThrowException()

        requestHandler.handleRequest(createMockRequest())

        verify {
            hibernateSessionContextManager.closeSessionContextExceptionally()
        }
    }

    private fun makeRequestHandlingThrowException(exception: Exception = Exception()) {
        every { endpointFactory.getEndpoint(any()) } throws exception
    }

    private fun makeExceptionHandlerReturn(returns: Response = createMockResponse()) {
        every { exceptionHandler.handleException(any(), any()) } returns returns
    }

    private fun assertRequestHandlerReturns(response: Response) {
        assertEquals(
            response,
            requestHandler.handleRequest(createMockRequest()))
    }
}