package app.repos

import app.entities.FinishedVisit
import app.entities.OngoingVisit
import app.entities.Payment
import minutesAgo
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

    @Nested
    @DisplayName("Test findOngoingVisitByTicketCode")
    inner class TestFindOngoingVisitByTicketCode {
        @Test
        fun `When ongoingVisit with the ticket code exists, returns it`() {
            val ongoingVisit = OngoingVisit().apply { ticketCode = 1234567 }
            val expectedId = session.save(ongoingVisit)

            val actualId = visitRepository
                .findOngoingVisitByTicketCode(1234567)
                ?.id

            assertEquals(expectedId, actualId)
        }

        @Test
        fun `When ongoingVisit with the ticket code does not exist, returns null`() {
            val ongoingVisit = visitRepository.findOngoingVisitByTicketCode(1234567)

            assertNull(ongoingVisit)
        }
    }

    @Nested
    @DisplayName("Test finishOngoingVisit")
    inner class TestFinishOngoingVisit {
        private lateinit var ongoingVisit: OngoingVisit

        @BeforeEach
        fun init() {
            val ongoingVisit = OngoingVisit().apply {
                entryTime = 10.minutesAgo
                ticketCode = 1234567
                payments = listOf(
                    Payment().apply {
                        this.amount = 100.0
                        this.madeAt = 5.minutesAgo
                    },
                    Payment().apply {
                        this.amount = 100.0
                        this.madeAt = 5.minutesAgo
                    },
                )
            }
            val id = session.save(ongoingVisit)

            this.ongoingVisit = session.get(OngoingVisit::class.java, id)
        }

        @Test
        fun `Deletes ongoing visit from database`() {
            visitRepository.finishOngoingVisit(ongoingVisit)

            assertNull(session.get(OngoingVisit::class.java, ongoingVisit.id))
        }

        @Test
        fun `Inserts a finished visit record to the database`() {
            visitRepository.finishOngoingVisit(ongoingVisit)

            val finishOngoingVisits = session.createQuery(
                "from FinishedVisit"
            ).list() as List<FinishedVisit>

            assertTrue(finishOngoingVisits.isNotEmpty())
        }

        @Test
        fun `Inserted FinishedVisit shares properties with OngoingVisit being finished`() {
            visitRepository.finishOngoingVisit(ongoingVisit)

            val finishOngoingVisits = session.createQuery(
                "from FinishedVisit"
            ).uniqueResult() as FinishedVisit

            assertEquals(ongoingVisit.ticketCode, finishOngoingVisits.ticketCode)
            assertEquals(ongoingVisit.entryTime, finishOngoingVisits.entryTime)
            assertEquals(ongoingVisit.payments, finishOngoingVisits.payments)
        }
    }
}