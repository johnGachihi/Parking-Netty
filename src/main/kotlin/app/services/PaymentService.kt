package app.services

import app.entities.OngoingVisit
import app.entities.Payment
import app.repos.ParkingFeeConfigRepo
import java.time.Duration
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
            val timeOfStay = getTimeOfStay(ongoingVisit)
            calculateFee(timeOfStay)
        } else {
            return if (isLatestPaymentExpired(ongoingVisit)) {
                val timeOfStay = getTimeOfStay(ongoingVisit)
                calculateFee(timeOfStay) - getTotalAmountPaid(ongoingVisit)
            } else {
                0.0
            }
        }
    }

    private fun calculateFee(timeOfStay: Duration): Double {
        val overlappingTariff = parkingTariffService.getOverlappingTariff(timeOfStay)
        return if (overlappingTariff != null)
            overlappingTariff.fee
        else {
            parkingTariffService.getHighestTariff()?.fee ?: 0.0
        }
    }

    private fun getTimeOfStay(ongoingVisit: OngoingVisit): Duration {
        val minutesSinceEntry: Long =
            ongoingVisit.entryTime.until(Instant.now(), ChronoUnit.MINUTES)
        return Duration.ofMinutes(minutesSinceEntry)
    }

    private fun getTotalAmountPaid(ongoingVisit: OngoingVisit) =
        ongoingVisit.payments.fold(0.0) { acc, payment -> acc + payment.amount!! }

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
