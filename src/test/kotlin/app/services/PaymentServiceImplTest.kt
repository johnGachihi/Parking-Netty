package app.services

import app.entities.OngoingVisit
import app.entities.ParkingTariff
import app.entities.Payment
import app.repos.ParkingFeeConfigRepo
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import minutesAgo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration
import java.time.Instant

@ExtendWith(MockKExtension::class)
internal class PaymentServiceImplTest {
    @RelaxedMockK
    private lateinit var parkingTariffService: ParkingTariffService

    @RelaxedMockK
    private lateinit var parkingFeeConfigRepo: ParkingFeeConfigRepo

    @InjectMockKs
    private lateinit var paymentService: PaymentServiceImpl

    @Nested
    @DisplayName("When no payments have been made for ongoing visit")
    inner class TestWhenNoPaymentsMadeForOngoingVisit {
        @Test
        fun `then use time-of-stay and parking tariffs to calculate fee`() {
            val ongoingVisit = OngoingVisit().apply {
                entryTime = 22.minutesAgo
            }

            paymentService.calculateFee(ongoingVisit)

            verify {
                parkingTariffService.getFee(Duration.ofMinutes(22))
            }
        }

        @Test
        fun `returns fee as returned by parkingTariffService`() {
            val ongoingVisit = OngoingVisit().apply {
                entryTime = 22.minutesAgo
            }
            every {
                parkingTariffService.getFee(Duration.ofMinutes(22))
            } returns 1234.0

            val fee = paymentService.calculateFee(ongoingVisit)

            assertEquals(1234.0, fee)
        }
    }

    @Nested
    @DisplayName("When at least one payment has been made for ongoing visit")
    inner class TestWhenPaymentsMadeForOngoingVisit {
        @Test
        @DisplayName(
            "and latest payment has expired, returns fee as returned by" +
                    "parkingTariffService less the total amount already paid"
        )
        fun `test when latest payment has expired`() {
            val ongoingVisit = OngoingVisit().apply {
                entryTime = 20.minutesAgo
                payments = listOf(
                    makePayment(madeAt = 10.minutesAgo, amount = 100.0),
                    makePayment(madeAt = 8.minutesAgo, amount = 100.0),
                    makePayment(madeAt = 6.minutesAgo, amount = 100.0)
                )
            }
            every {
                parkingTariffService.getFee(Duration.ofMinutes(20))
            } returns 1234.0

            every {
                parkingFeeConfigRepo.paymentExpirationTimeSpan
            } returns Duration.ofMinutes(1)

            val fee = paymentService.calculateFee(ongoingVisit)

            assertEquals((1234.0 - (100.0 + 100.0 + 100.0)), fee)
        }

        @Test
        fun `and latest payment has not expired, then returns a fee of 0`() {
            val ongoingVisit = OngoingVisit().apply {
                entryTime = 20.minutesAgo
                payments = listOf(
                    makePayment(madeAt = 22.minutesAgo),
                    makePayment(madeAt = 20.minutesAgo),
                )
            }
            every {
                parkingFeeConfigRepo.paymentExpirationTimeSpan
            } returns Duration.ofMinutes(21)

            val fee = paymentService.calculateFee(ongoingVisit)

            assertEquals(0.0, fee)
        }
    }

    private fun makeParkingTariff(upperLimit: Duration = Duration.ofMinutes(1), fee: Double) =
        ParkingTariff().apply {
            this.upperLimit = upperLimit
            this.fee = fee
        }

    private fun makePayment(madeAt: Instant, amount: Double = 100.0) =
        Payment().apply {
            this.amount = amount
            this.madeAt = madeAt
        }

    private fun makeOngoingVisitWithNoPayments() =
        OngoingVisit().apply {
            this.payments = emptyList()
        }
}