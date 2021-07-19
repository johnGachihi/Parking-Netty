package db

import app.db.HibernateSessionContextManager
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction
import org.hibernate.context.internal.ManagedSessionContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
@DisplayName("Test HibernateSessionContextManager")
internal class HibernateSessionContextManagerTest {
    @RelaxedMockK
    private lateinit var newSession: Session

    @RelaxedMockK
    private lateinit var currentSession: Session

    @RelaxedMockK
    private lateinit var currentSessionTransaction: Transaction

    @RelaxedMockK
    private lateinit var sessionFactory: SessionFactory

    @InjectMockKs
    private lateinit var hibernateSessionContextManager: HibernateSessionContextManager

    @BeforeEach
    fun setup() {
        every { currentSession.transaction } returns currentSessionTransaction

        every { sessionFactory.openSession() } returns newSession
        every { sessionFactory.currentSession } returns currentSession
    }

    @Test
    fun `OnRequestReceived, opens a hibernate session and starts a transaction`() {
        hibernateSessionContextManager.onRequestReceived()

        verify(exactly = 1) { sessionFactory.openSession() }
        verify(exactly = 1) { newSession.beginTransaction() }
    }

    @Test
    fun `OnRequestReceived, binds the opened session to the hibernate Session context`() {
        mockkStatic(ManagedSessionContext::class)

        hibernateSessionContextManager.onRequestReceived()

        verify(exactly = 1) { ManagedSessionContext.bind(any()) }
    }

    @Test
    fun `OnRequestHandled, flushes changes, commits the transaction and closes the session`() {
        hibernateSessionContextManager.onRequestHandled()

        verify(exactly = 1) { currentSession.flush() }
        verify(exactly = 1) { currentSessionTransaction.commit() }
        verify(exactly = 1) { currentSession.close() }
    }

    @Test
    fun `OnRequestHandled, unbinds the current hibernate session from the hibernate session context`() {
        mockkStatic(ManagedSessionContext::class)

        hibernateSessionContextManager.onRequestHandled()

        verify { ManagedSessionContext.unbind(sessionFactory) }
    }

    @Nested
    @DisplayName("When RequestHandledExceptionally, event published")
    inner class TestOnRequestHandledExceptionally {
        @Test
        fun `rolls back transaction then closes session`() {
            hibernateSessionContextManager.onRequestHandledExceptionally()

            verifyOrder {
                currentSessionTransaction.rollback()
                currentSession.close()
            }
        }

        @Test
        fun `unbinds the currentSession from the hibernate session context`() {
            mockkStatic(ManagedSessionContext::class)

            hibernateSessionContextManager.onRequestHandledExceptionally()

            verify(exactly = 1) { ManagedSessionContext.unbind(sessionFactory) }
        }
    }
}