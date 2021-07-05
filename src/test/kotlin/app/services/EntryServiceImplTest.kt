package app.services

import app.IllegalDataException
import app.repos.VisitRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class EntryServiceImplTest {
    @RelaxedMockK
    lateinit var visitRepository: VisitRepository

    @InjectMockKs
    lateinit var entryService: EntryServiceImpl

    @Test
    fun `When ticketCode is in use, then throws IllegalDataException`() {
        val ticketCode = 123L
        every { visitRepository.onGoingVisitExistsWithTicketCode(ticketCode) } returns true

        val exception = assertThrows(IllegalDataException::class.java) {
            entryService.addVisit(ticketCode)
        }
        assertEquals(
            "The ticket code provided is already in use.",
            exception.message
        )
    }

    @Test
    fun `When ticket code is not in use, then saves new entry`() {
        val ticketCode = 123L
        every { visitRepository.onGoingVisitExistsWithTicketCode(ticketCode) } returns false

        entryService.addVisit(ticketCode)

        verify(exactly = 1) {
            visitRepository.saveOnGoingVisit(match { it.ticketCode == ticketCode })
        }
    }
}