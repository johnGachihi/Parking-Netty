package core.router

import core.Response
import core.exceptionhandling.ExceptionHandler
import core.requesthandling.RequestHandlerEventManager
import core.requesthandling.RequestHandlerImpl
import createMockRequest
import createMockResponse
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class RequestHandlerImplTest {
    @RelaxedMockK
    private lateinit var eventManager: RequestHandlerEventManager

    @RelaxedMockK
    private lateinit var endpointFactory: EndpointFactory

    @RelaxedMockK
    private lateinit var exceptionHandler: ExceptionHandler

    @InjectMockKs
    private lateinit var requestHandler: RequestHandlerImpl

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
    fun `Publishes 'RequestReceived' event, before attempting to handle request`() {
        requestHandler.handleRequest(createMockRequest())

        verifyOrder {
            eventManager.notifyRequestReceived()
            endpointFactory.getEndpoint(any())
        }
    }

    @Test
    fun `Publishes 'RequestHandled' event, after Request is handled`() {
        requestHandler.handleRequest(createMockRequest())

        verifyOrder {
            endpointFactory.getEndpoint(any())
            eventManager.notifyRequestHandled()
        }
    }

    @Test
    @DisplayName(
        "When request handling fails, the 'RequestHandledExceptionally'" +
                "event is published after the request-handling attempt"
    )
    fun `test RequestHandledExceptionally event published`() {
        makeRequestHandlingThrowException()

        requestHandler.handleRequest(createMockRequest())

        verifyOrder {
            endpointFactory.getEndpoint(any())
            eventManager.notifyRequestHandleExceptionally()
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
            requestHandler.handleRequest(createMockRequest())
        )
    }
}