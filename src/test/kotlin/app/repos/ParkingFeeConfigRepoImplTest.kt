package app.repos

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration
import kotlin.test.assertIs

@ExtendWith(MockKExtension::class)
internal class ParkingFeeConfigRepoImplTest {
    @RelaxedMockK
    private lateinit var configRepoHelper: ConfigRepoHelper

    @InjectMockKs
    private lateinit var parkingFeeConfigRepo: ParkingFeeConfigRepoImpl

    @Nested
    @DisplayName("Test paymentExpirationTimeSpan")
    inner class PaymentExpirationTimeSpanTest {
        @Test
        fun `When configuration not set, returns default`() {
            setConfiguration("payment_expiration_time_span", null)

            assertEquals(
                Duration.ofMinutes(20), // Default
                parkingFeeConfigRepo.paymentExpirationTimeSpan
            )
        }

        @Nested
        @DisplayName("When configuration is set")
        inner class WhenSet {
            @Test
            fun `but cannot be converted to an integer, then throws IllegalStateException`() {
                val value = "a string"
                setConfiguration("payment_expiration_time_span", value)

                val exception = assertThrows<IllegalStateException> {
                    parkingFeeConfigRepo.paymentExpirationTimeSpan
                }
                assertEquals(
                    "Invalid `Payment expiration time span` setting ($value). Not a number.",
                    exception.message
                )
                assertIs<NumberFormatException>(exception.cause)
            }

            @Test
            fun `and can be converted to an integer, then returns it`() {
                setConfiguration("payment_expiration_time_span", "100")

                assertEquals(
                    Duration.ofMinutes(100),
                    parkingFeeConfigRepo.paymentExpirationTimeSpan,
                )
            }
        }
    }

    private fun setConfiguration(key: String, value: String?) {
        every { configRepoHelper.getValue(key) } returns value
    }
}