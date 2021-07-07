package app.repos

import app.utils.Minutes
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import java.lang.NumberFormatException
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
                Minutes(20), // Default
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
                    "Invalid `Payment expiration time span` setting ($value). Not an integer.",
                    exception.message
                )
                assertIs<NumberFormatException>(exception.cause)
            }

            @Test
            fun `and can be converted to an integer, then returns it`() {
                setConfiguration("payment_expiration_time_span", "100")

                assertEquals(
                    parkingFeeConfigRepo.paymentExpirationTimeSpan,
                    Minutes(100)
                )
            }
        }
    }

    private fun setConfiguration(key: String, value: String?) {
        every { configRepoHelper.getValue(key) } returns value
    }
}