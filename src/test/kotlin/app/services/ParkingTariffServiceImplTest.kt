package app.services

import app.entities.ParkingTariff
import app.repos.ParkingTariffRepo
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration

@ExtendWith(MockKExtension::class)
internal class ParkingTariffServiceImplTest {
    @RelaxedMockK
    private lateinit var parkingTariffRepo: ParkingTariffRepo

    @InjectMockKs
    private lateinit var parkingTariffService: ParkingTariffServiceImpl

    @Nested
    @DisplayName("Test getFee")
    inner class TestGetFee {
        @Nested
        @DisplayName("When there is parking tariff data")
        inner class TestWhenThereIsParkingTariffData {
            @BeforeEach
            fun init() {
                every {
                    parkingTariffRepo.getAllInAscendingOrder()
                } returns listOf(
                    makeParkingTariff(1, Duration.ofMinutes(10), fee = 1.0),
                    makeParkingTariff(2, Duration.ofMinutes(20), fee = 2.0),
                    makeParkingTariff(3, Duration.ofMinutes(30), fee = 3.0),
                )
            }

            @Test
            fun `and there is an overlapping tariff, return the overlapping tariff's fee`() {
                val fee = parkingTariffService.getFee(Duration.ofMinutes(19))

                assertEquals(2.0, fee)
            }

            @Test
            fun `and there is no overlapping tariff, returns the fee for the tariff with the highest upperLimit`() {
                val fee = parkingTariffService.getFee(Duration.ofMinutes(40))

                assertEquals(3.0, fee)
            }
        }

        @Test
        fun `When there is no parking tariff data, returns 0`() {
            val fee = parkingTariffService.getFee(Duration.ofMinutes(40))

            assertEquals(0.0, fee)
        }
    }

    private fun makeParkingTariff(id: Long, upperLimit: Duration, fee: Double = 1.0) =
        ParkingTariff().apply {
            this.id = id
            this.upperLimit = upperLimit
            this.fee = fee
        }
}