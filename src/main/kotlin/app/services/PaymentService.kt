package app.services

import app.entities.OngoingVisit
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
        return if (ongoingVisit.payments.isEmpty()) {
            parkingTariffService.getFee(ongoingVisit.timeOfStay)
        } else {
            return if (isLatestPaymentExpired(ongoingVisit)) {
                parkingTariffService.getFee(ongoingVisit.timeOfStay) - ongoingVisit.totalAmountPaid
            } else {
                0.0
            }
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
