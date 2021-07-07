import core.Request
import core.Response
import java.time.Instant
import java.time.temporal.ChronoUnit

fun createMockRequest(withActionCode: Int? = null): Request {
    return object : Request {
        override val actionCode = withActionCode ?: -1
    }
}

fun createMockResponse(): Response {
    return object : Response {}
}

val Int.minutesAgo: Instant
    get() = Instant.now().minus(this.toLong(), ChronoUnit.MINUTES)