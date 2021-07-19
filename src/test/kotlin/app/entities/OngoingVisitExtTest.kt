package app.entities

import app.entities.visit.OngoingVisit
import minutesAgo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Duration

internal class OngoingVisitExtTest {
    @Test
    fun `test timeOfStay`() {
        val ongoingVisit = OngoingVisit().apply {
            entryTime = 20.minutesAgo
        }
        assertEquals(Duration.ofMinutes(20), ongoingVisit.timeOfStay)
    }

    @Test
    fun `test totalAmountPaid`() {
        val ongoingVisit = OngoingVisit().apply {
            payments = listOf(
                Payment().apply { amount = 1.0 },
                Payment().apply { amount = 1.0 },
                Payment().apply { amount = 1.0 }
            )
        }

        assertEquals(3.0, ongoingVisit.totalAmountPaid)
    }
}