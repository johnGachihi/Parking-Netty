package app.repos

import app.entities.ParkingTariff
import org.hibernate.Session

interface ParkingTariffRepo {
    fun getAllInAscendingOrder(): List<ParkingTariff>
}

class HibernateParkingTariffRepo(
    private val session: Session
) : ParkingTariffRepo {
    override fun getAllInAscendingOrder(): List<ParkingTariff> {
        @Suppress("UNCHECKED_CAST")
        return session.createQuery(
            "from ParkingTariff p order by p.upperLimit"
        ).setCacheable(true).list() as List<ParkingTariff>
    }
}