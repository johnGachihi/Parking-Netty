package app.repos

import app.entities.FinishedVisit
import app.entities.OngoingVisit
import org.hibernate.Session
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(HibernateSessionExtension::class)
internal class VisitRepositoryTest {
    lateinit var session: Session

    lateinit var visitRepository: VisitRepository

    @BeforeEach
    fun init() {
        visitRepository = VisitRepositoryImpl(session)
    }

    @Nested
    @DisplayName("Test onGoingVisitExistsWithTicketCode")
    inner class OnGoingVisitExistsWithTicketCodeTest {
        @Test
        fun `When ongoing visit with the ticket code exists, then returns true`() {
            session.save(OngoingVisit().apply { ticketCode = 123L })
            session.flush()

            assertTrue(
                visitRepository.onGoingVisitExistsWithTicketCode(123)
            )
        }
        @Test
        fun `When no ongoing visit exists with the ticket code, then returns false`() {
            session.save(FinishedVisit().apply { ticketCode = 123L })
            session.flush()

            assertFalse(
                visitRepository.onGoingVisitExistsWithTicketCode(123L)
            )
        }
    }

    @Test
    fun `Test saveOngoingVisit`() {
        val id = visitRepository.saveOnGoingVisit(
            OngoingVisit().apply { ticketCode = 123 })

        assertNotNull(session.get(OngoingVisit::class.java, id))
    }
}