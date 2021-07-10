package app.services

import app.entities.ParkingTariff
import app.repos.ParkingTariffRepo
import app.utils.Minutes

interface ParkingTariffService {
    fun getOverlappingTariff(duration: Minutes): ParkingTariff?
}

class ParkingTariffServiceImpl(
    private val parkingTariffRepo: ParkingTariffRepo
) : ParkingTariffService {
    override fun getOverlappingTariff(duration: Minutes): ParkingTariff? {
        val tariffs = parkingTariffRepo.getAllInAscendingOrder()

        return tariffs.find { it.upperLimit.minutes > duration.minutes }
            ?: getHighestTariff(tariffs)
    }

    private fun getHighestTariff(tariffs: List<ParkingTariff>): ParkingTariff? =
        tariffs.maxByOrNull { it.upperLimit.minutes }
}