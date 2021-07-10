package app.services

import app.entities.OngoingVisit
import app.utils.Minutes
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.math.min

@ExtendWith(MockKExtension::class)
internal class PaymentServiceImplTest {
    @MockK
    private lateinit var parkingTariffService: ParkingTariffService

    @MockK
    private lateinit var ongoingVisitHelper: OngoingVisitHelper

    @InjectMockKs
    private lateinit var paymentService: PaymentServiceImpl

    @Nested
    @DisplayName("When no payments have been made for ongoing visit")
    inner class TestWhenNoPaymentsMadeForOngoingVisit {
        private lateinit var ongoingVisit: OngoingVisit

        @BeforeEach
        fun init() { ongoingVisit = OngoingVisit() }

        @Test
        @DisplayName("then use time-of-stay and parking tariffs to calculate fee")
        fun test() {
            val ongoingVisit = OngoingVisit().apply {
                entryTime = minutesAgo(22)
            }

            paymentService.calculateFee(ongoingVisit)

            verify {
                parkingTariffService.getOverlappingTariff(Minutes(20))
            }
        }
    }

    private fun makeOngoingVisitWithNoPayments() =
        OngoingVisit().apply {
            this.payments = emptyList()
        }

    private fun minutesAgo(n: Int): Instant =
        Instant.now().minus(n.toLong(), ChronoUnit.MINUTES)
}