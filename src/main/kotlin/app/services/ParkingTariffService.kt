package app.services

import app.entities.ParkingTariff
import app.repos.ParkingTariffRepo
import java.time.Duration

interface ParkingTariffService {
    fun getFee(duration: Duration): Double
}

class ParkingTariffServiceImpl(
    private val parkingTariffRepo: ParkingTariffRepo
) : ParkingTariffService {

    private val orderedParkingTariffs: List<ParkingTariff> by lazy {
        parkingTariffRepo.getAllInAscendingOrder()
    }

    override fun getFee(duration: Duration): Double {
        return if (orderedParkingTariffs.isNotEmpty())
            getOverlappingTariff(duration)?.fee
                ?: getHighestTariff()!!.fee
        else
            0.0
    }

    private fun getOverlappingTariff(duration: Duration): ParkingTariff? =
        orderedParkingTariffs.find { it.upperLimit > duration }

    private fun getHighestTariff() =
        parkingTariffRepo.getAllInAscendingOrder().lastOrNull()
}