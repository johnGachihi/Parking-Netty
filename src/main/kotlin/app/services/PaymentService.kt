package app.services

import app.entities.OngoingVisit
import app.utils.Minutes
import org.hibernate.dialect.Dialect
import org.hibernate.type.AbstractSingleColumnStandardBasicType
import org.hibernate.type.DiscriminatorType
import java.util.*

interface PaymentService {
    fun calculateFee(ongoingVisit: OngoingVisit): Double
}

class PaymentServiceImpl(
    private val parkingTariffService: ParkingTariffService,
    private val ongoingVisitHelper: OngoingVisitHelper
) : PaymentService {
    override fun calculateFee(ongoingVisit: OngoingVisit): Double {
        return -1.0
    }
}
