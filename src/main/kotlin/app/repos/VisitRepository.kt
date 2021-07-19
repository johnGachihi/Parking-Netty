package app.repos

import app.entities.visit.FinishedVisit
import app.entities.visit.OngoingVisit
import org.hibernate.Session

// TODO: Make OngoingVisitRepository
interface VisitRepository {
    fun saveOnGoingVisit(ongoingVisit: OngoingVisit): Long
    fun onGoingVisitExistsWithTicketCode(ticketCode: Long): Boolean
    fun findOngoingVisitByTicketCode(ticketCode: Long): OngoingVisit?
    fun finishOngoingVisit(ongoingVisit: OngoingVisit)
}

class VisitRepositoryImpl(
    private val session: Session
) : VisitRepository {
    override fun saveOnGoingVisit(ongoingVisit: OngoingVisit): Long {
        return session.save(ongoingVisit) as Long
    }

    override fun onGoingVisitExistsWithTicketCode(ticketCode: Long): Boolean {
        val count: Long = session.createQuery(
            "SELECT count(v) " +
                    "FROM OngoingVisit v " +
                    "WHERE v.ticketCode = :ticketCode"
        )
            .setParameter("ticketCode", ticketCode)
            .uniqueResult() as Long

        return count > 0
    }

    override fun findOngoingVisitByTicketCode(ticketCode: Long): OngoingVisit? {
        return session.createQuery(
            "SELECT v " +
                    "FROM OngoingVisit v " +
                    "WHERE v.ticketCode = :ticketCode"
        )
            .setParameter("ticketCode", ticketCode)
            .uniqueResult() as OngoingVisit?
    }

    override fun finishOngoingVisit(ongoingVisit: OngoingVisit) {
        session.delete(ongoingVisit)
        session.save(FinishedVisit().apply {
            entryTime = ongoingVisit.entryTime
            ticketCode = ongoingVisit.ticketCode
            payments = ongoingVisit.payments
        })
    }
}