package app.services

import app.entities.OngoingVisit
import app.entities.ParkingTariff
import app.entities.Payment
import app.repos.ParkingFeeConfigRepo
import app.utils.Minutes
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import minutesAgo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

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
        private lateinit var ongoingVisit: OngoingVisit

        @BeforeEach
        fun init() {
            ongoingVisit = OngoingVisit()
        }

        @Test
        fun `then use time-of-stay and parking tariffs to calculate fee`() {
            val ongoingVisit = OngoingVisit().apply {
                entryTime = 22.minutesAgo
            }

            paymentService.calculateFee(ongoingVisit)

            verify {
                parkingTariffService.getOverlappingTariff(Minutes(22))
            }
        }

        @Test
        fun `and there is an overlapping tariff for visit's time-of-stay, return fee as it gets it from the overlapping tariffs`() {
            val ongoingVisit = OngoingVisit().apply {
                entryTime = 22.minutesAgo
            }
            every {
                parkingTariffService.getOverlappingTariff(Minutes(22))
            } returns makeParkingTariff(fee = 1234.0)

            val fee = paymentService.calculateFee(ongoingVisit)

            assertEquals(1234.0, fee)
        }

        @Nested
        @DisplayName("and there is no overlapping tariff for visit's time-of-stay")
        inner class TestThereIsNoOverlappingTariff {
            @Test
            fun `but there is a tariff with highest limit, returns fee for tariff with highest upperLimit`() {
                val ongoingVisit = OngoingVisit().apply {
                    entryTime = 22.minutesAgo
                }
                every { parkingTariffService.getOverlappingTariff(Minutes(22)) } returns null
                every { parkingTariffService.getHighestTariff() } returns makeParkingTariff(fee = 1234.0)

                val fee = paymentService.calculateFee(ongoingVisit)

                assertEquals(1234.0, fee)
            }

            @Test
            fun `and there is no tariff with highest limit, returns 0`() {
                val ongoingVisit = OngoingVisit().apply {
                    entryTime = 22.minutesAgo
                }
                every { parkingTariffService.getOverlappingTariff(Minutes(22)) } returns null
                every { parkingTariffService.getHighestTariff() } returns null

                val fee = paymentService.calculateFee(ongoingVisit)

                assertEquals(0.0, fee)
            }
        }
    }

    @Nested
    @DisplayName("When payments have been made for ongoing visit")
    inner class TestWhenPaymentsMadeForOngoingVisit {
        @Test
        @DisplayName(
            "if latest payment has expired, returns fee calculated from" +
                    "time-of-stay and parking tariffs less the total amount already paid"
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
                parkingTariffService.getOverlappingTariff(Minutes(20))
            } returns makeParkingTariff(fee = 1234.0)

            every {
                parkingFeeConfigRepo.paymentExpirationTimeSpan
            } returns returnValueClass(Minutes(1))

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
            } returns returnValueClass(Minutes(21))

            val fee = paymentService.calculateFee(ongoingVisit)

            assertEquals(0.0, fee)
        }
    }

    private fun makeParkingTariff(upperLimit: Minutes = Minutes(1), fee: Double) =
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



    fun <T : Any> returnValueClass(value: T): T {
        require(value::class.isValue)
        val constructor = value::class.primaryConstructor!!
        val constructorParameter = constructor.parameters[0]
        val memberProperty = value::class.declaredMemberProperties
            .first { it.name == constructorParameter.name }
            .apply { isAccessible = true }
            .let @Suppress("UNCHECKED_CAST") { it as KProperty1<T, T> }
        return memberProperty.get(value)
    }
}