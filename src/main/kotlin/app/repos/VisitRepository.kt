package app.repos

import app.entities.OngoingVisit
import org.hibernate.Session

interface VisitRepository {
    fun saveOnGoingVisit(ongoingVisit: OngoingVisit): Long
    fun onGoingVisitExistsWithTicketCode(ticketCode: Long): Boolean
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
}