package app.services

import app.entities.OngoingVisit
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

internal class OngoingVisitHelperImplTest {
    @Test
    fun test() {
        val ongoingVisit = OngoingVisit().apply { entryTime = minutesAgo(20) }

        val timeOfStay = OngoingVisitHelperImpl().getTimeOfStay(ongoingVisit)

        assertEquals(20, timeOfStay.minutes)
    }

    private fun minutesAgo(n: Int): Instant =
        Instant.now().minus(n.toLong(), ChronoUnit.MINUTES)
}