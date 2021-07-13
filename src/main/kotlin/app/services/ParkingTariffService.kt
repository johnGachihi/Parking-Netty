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

    private val orderedParkingTariffs: List<ParkingTariff> by lazy {
        parkingTariffRepo.getAllInAscendingOrder()
    }

    override fun getOverlappingTariff(duration: Duration): ParkingTariff? =
        orderedParkingTariffs.find { it.upperLimit > duration }

    override fun getHighestTariff() =
        parkingTariffRepo.getAllInAscendingOrder().lastOrNull()
}