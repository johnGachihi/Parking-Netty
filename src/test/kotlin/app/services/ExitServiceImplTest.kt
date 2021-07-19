package app.services

import app.IllegalDataException
import app.UnservicedFeeException
import app.entities.visit.OngoingVisit
import app.repos.VisitRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
@DisplayName("Test ExitServiceImpl")
internal class ExitServiceImplTest {
    @MockK
    private lateinit var paymentService: PaymentService

    @RelaxedMockK
    private lateinit var visitRepository: VisitRepository

    @InjectMockKs
    private lateinit var exitService: ExitServiceImpl

    @Test
    fun `When ticket code provided is not for an OngoingVisit, throw IllegalDataException`() {
        every { visitRepository.findOngoingVisitByTicketCode(any()) } returns null

        assertThrows(IllegalDataException::class.java) {
            exitService.finishVisit(1234567)
        }
    }

    @Nested
    @DisplayName("When provided ticket code is for an OngoingVisit")
    inner class TestWhenProvidedTicketCodeIsForAnOngoingVisit {
        private val ongoingVisit = OngoingVisit()

        @BeforeEach
        fun init() {
            every {
                visitRepository.findOngoingVisitByTicketCode(any())
            } returns ongoingVisit
        }

        @Test
        fun `and its parking fee is greater than 0, then throw UnservicedParkingFee exception`() {
            every { paymentService.calculateFee(any()) } returns 1.0

            assertThrows(UnservicedFeeException::class.java) {
                exitService.finishVisit(1234)
            }
        }

        @Test
        fun `and its parking fee is not greater than 0, then finishes the ongoingVisit`() {
            every { paymentService.calculateFee(ongoingVisit) } returns 0.0

            exitService.finishVisit(1234567)

            verify { visitRepository.finishOngoingVisit(ongoingVisit) }
        }
    }
}