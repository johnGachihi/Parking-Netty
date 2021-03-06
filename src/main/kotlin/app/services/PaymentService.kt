package app.services

import app.entities.visit.OngoingVisit
import app.entities.Payment
import app.entities.timeOfStay
import app.entities.totalAmountPaid
import app.repos.ParkingFeeConfigRepo
import java.time.Instant
import java.time.temporal.ChronoUnit

interface PaymentService {
    fun calculateFee(ongoingVisit: OngoingVisit): Double
}

class PaymentServiceImpl(
    private val parkingTariffService: ParkingTariffService,
    private val parkingFeeConfigRepo: ParkingFeeConfigRepo
) : PaymentService {
    override fun calculateFee(ongoingVisit: OngoingVisit): Double {
        return if (ongoingVisit.payments.isNotEmpty() && !isLatestPaymentExpired(ongoingVisit)) {
            0.0
        } else {
            parkingTariffService.getFee(ongoingVisit.timeOfStay) - ongoingVisit.totalAmountPaid
        }
    }

    private fun isLatestPaymentExpired(ongoingVisit: OngoingVisit): Boolean {
        val latestPayment = getLatestPayment(ongoingVisit)
        return isExpired(latestPayment)
    }

    private fun getLatestPayment(ongoingVisit: OngoingVisit): Payment {
        return ongoingVisit.payments.maxByOrNull { it.madeAt }!!
    }

    private fun isExpired(payment: Payment): Boolean {
        val timeSincePayment = payment.madeAt.until(Instant.now(), ChronoUnit.MINUTES)
        val expirationTimeSpan = parkingFeeConfigRepo.paymentExpirationTimeSpan.toMinutes()
        return  timeSincePayment > expirationTimeSpan
    }
}
