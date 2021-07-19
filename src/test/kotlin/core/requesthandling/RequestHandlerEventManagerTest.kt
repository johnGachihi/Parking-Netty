package core.requesthandling

import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@DisplayName("Test RequestHandlingEventManager")
@ExtendWith(MockKExtension::class)
internal class RequestHandlerEventManagerTest {
    @RelaxedMockK
    private lateinit var requestHandlerListenerOne: RequestHandlerListener
    @RelaxedMockK
    private lateinit var requestHandlerListenerTwo: RequestHandlerListener

    private lateinit var requestHandlerEventManager: RequestHandlerEventManager

    @BeforeEach
    fun init() {
        requestHandlerEventManager = RequestHandlerEventManager()
        requestHandlerEventManager.subscribe(requestHandlerListenerOne)
        requestHandlerEventManager.subscribe(requestHandlerListenerTwo)
    }

    @Test
    fun `Test notifyRequestReceived`() {
        requestHandlerEventManager.notifyRequestReceived()

        verify(exactly = 1) { requestHandlerListenerOne.onRequestReceived() }
        verify(exactly = 1) { requestHandlerListenerTwo.onRequestReceived() }
    }

    @Test
    fun `Test notifyRequestHandled`() {
        requestHandlerEventManager.notifyRequestHandled()

        verify(exactly = 1) { requestHandlerListenerOne.onRequestHandled() }
        verify(exactly = 1) { requestHandlerListenerTwo.onRequestHandled() }
    }

    @Test
    fun `Test notifyRequestHandledExceptionally`() {
        requestHandlerEventManager.notifyRequestHandleExceptionally()

        verify(exactly = 1) { requestHandlerListenerOne.onRequestHandledExceptionally() }
        verify(exactly = 1) { requestHandlerListenerTwo.onRequestHandledExceptionally() }
    }
}