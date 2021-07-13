package app.repos

import java.time.Duration

interface ParkingFeeConfigRepo {
    val paymentExpirationTimeSpan: Duration
}


class ParkingFeeConfigRepoImpl(
    private val configRepoHelper: ConfigRepoHelper
) : ParkingFeeConfigRepo {

    override val paymentExpirationTimeSpan: Duration
        get() {
            val value = configRepoHelper.getValue("payment_expiration_time_span")
                ?: return Duration.ofMinutes(20) // Default value

            try {
                return Duration.ofMinutes(value.toLong())
            } catch (e: NumberFormatException) {
                throw IllegalStateException(
                    "Invalid `Payment expiration time span` setting ($value). Not a number.", e)
            }
        }
}