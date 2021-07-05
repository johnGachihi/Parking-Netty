package app.services

import app.IllegalDataException
import app.entities.OngoingVisit
import app.repos.VisitRepository

interface EntryService {
    fun addVisit(ticketCode: Long)
}

class EntryServiceImpl(
    private val visitRepository: VisitRepository
) : EntryService {
    override fun addVisit(ticketCode: Long) {
        if (isTicketCodeIsInUse(ticketCode))
            throw IllegalDataException("The ticket code provided is already in use.")

        val newVisit = OngoingVisit().apply { this.ticketCode = ticketCode }
        visitRepository.saveOnGoingVisit(newVisit)
    }

    private fun isTicketCodeIsInUse(ticketCode: Long): Boolean =
        visitRepository.onGoingVisitExistsWithTicketCode(ticketCode)
}