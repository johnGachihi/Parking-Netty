package app.services

import app.entities.ParkingTariff
import app.repos.ParkingTariffRepo
import java.time.Duration

interface ParkingTariffService {
    fun getOverlappingTariff(duration: Duration): ParkingTariff?
    fun getHighestTariff(): ParkingTariff?
}

class ParkingTariffServiceImpl(
    private val parkingTariffRepo: ParkingTariffRepo
) : ParkingTariffService {
    override fun getOverlappingTariff(duration: Duration): ParkingTariff? {
        val tariffs = parkingTariffRepo.getAllInAscendingOrder()

        return tariffs.find { it.upperLimit > duration }
    }

    override fun getHighestTariff() =
        parkingTariffRepo.getAllInAscendingOrder().lastOrNull()
}