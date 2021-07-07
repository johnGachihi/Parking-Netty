package app.services

import app.entities.ParkingTariff
import app.repos.ParkingTariffRepo
import app.utils.Minutes
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class ParkingTariffServiceImplTest {
    @MockK
    private lateinit var parkingTariffRepo: ParkingTariffRepo

    @InjectMockKs
    private lateinit var parkingTariffService: ParkingTariffServiceImpl

    @Nested
    @DisplayName("Test getOverlappingTariff")
    inner class TestGetOverlappingTariff {

        @Nested
        @DisplayName("When there is parking tariff data")
        inner class TestWhenThereIsTariffData {
            @BeforeEach
            fun initTariffs() {
                every {
                    parkingTariffRepo.getAllInAscendingOrder()
                } returns listOf(
                    makeParkingTariff(1, Minutes(10)),
                    makeParkingTariff(2, Minutes(20)),
                    makeParkingTariff(3, Minutes(30)),
                )
            }

            @Test
            fun `Returns first tariff with a larger upperLimit than the duration provided`() {
                var tariff = parkingTariffService.getOverlappingTariff(Minutes(9))
                assertNotNull(tariff)
                assertEquals(1, tariff!!.id)

                tariff = parkingTariffService.getOverlappingTariff(Minutes(22))
                assertNotNull(tariff)
                assertEquals(3, tariff!!.id)
            }

            @Test
            fun `and when duration is larger than the upperLimit for all tariffs, then returns null`() {
                val tariff = parkingTariffService.getOverlappingTariff(Minutes(100))
                assertNull(tariff)
            }
        }

        @Test
        fun `When there is no parking tariff data, then returns null`() {
            every { parkingTariffRepo.getAllInAscendingOrder() } returns emptyList()

            assertNull(parkingTariffService.getOverlappingTariff(Minutes(1)))
        }
    }

    @Nested
    @DisplayName("Test getHighestTariff")
    inner class TestGetHighestTariff {

        @Nested
        @DisplayName("When there is parking tariff data")
        inner class TestWhenThereIsParkingTariffData {
            @BeforeEach
            fun init() {
                every {
                    parkingTariffRepo.getAllInAscendingOrder()
                } returns listOf(
                    makeParkingTariff(1, Minutes(10)),
                    makeParkingTariff(2, Minutes(20)),
                    makeParkingTariff(3, Minutes(30)),
                )
            }

            @Test
            fun `returns tariff with highest upperLimit`() {
                val tariff = parkingTariffService.getHighestTariff()

                assertNotNull(tariff)
                assertEquals(3, tariff!!.id)
            }
        }

        @Test
        fun `When there is no parking tariff data, returns null`() {
            every { parkingTariffRepo.getAllInAscendingOrder() } returns emptyList()

            assertNull(parkingTariffService.getHighestTariff())
        }
    }

    private fun makeParkingTariff(id: Long, upperLimit: Minutes) =
        ParkingTariff().apply {
            this.id = id
            this.upperLimit = upperLimit
            fee = 1.0
        }
}