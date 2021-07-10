package app.services

import app.entities.OngoingVisit
import app.utils.Minutes
import java.time.Instant
import java.time.temporal.ChronoUnit

interface OngoingVisitHelper {
    fun getTimeOfStay(ongoingVisit: OngoingVisit): Minutes
}

class OngoingVisitHelperImpl : OngoingVisitHelper {
    override fun getTimeOfStay(ongoingVisit: OngoingVisit): Minutes {
        return Minutes(
            Instant.now().until(ongoingVisit.entryTime, ChronoUnit.MINUTES).toInt() * -1
        )
    }
}