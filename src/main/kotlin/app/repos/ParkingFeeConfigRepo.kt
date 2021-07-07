package app.repos

import app.utils.Minutes
import java.lang.IllegalStateException

interface ParkingFeeConfigRepo {
    val paymentExpirationTimeSpan: Minutes
}


class ParkingFeeConfigRepoImpl(
    private val configRepoHelper: ConfigRepoHelper
) : ParkingFeeConfigRepo {

    override val paymentExpirationTimeSpan: Minutes
        get() {
            val value = configRepoHelper.getValue("payment_expiration_time_span")
                ?: return Minutes(20) // Default value

            try {
                return Minutes(value.toInt())
            } catch (e: NumberFormatException) {
                throw IllegalStateException(
                    "Invalid `Payment expiration time span` setting ($value). Not an integer.", e)
            }
        }
}