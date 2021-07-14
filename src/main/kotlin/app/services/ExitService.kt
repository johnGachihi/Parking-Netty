package app.services

import app.IllegalDataException
import app.UnservicedFeeException
import app.repos.VisitRepository

interface ExitService {
    fun finishVisit(ticketCode: Long)
}

class ExitServiceImpl(
    private val visitRepository: VisitRepository,
    private val paymentService: PaymentService
) : ExitService {
    override fun finishVisit(ticketCode: Long) {
        val ongoingVisit = visitRepository.findOngoingVisitByTicketCode(ticketCode)
            ?: throw IllegalDataException()

        val fee = paymentService.calculateFee(ongoingVisit)
        if (fee > 0.0) {
            throw UnservicedFeeException()
        }

        visitRepository.finishOngoingVisit(ongoingVisit)
    }
}