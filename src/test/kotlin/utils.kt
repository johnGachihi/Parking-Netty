import core.Request
import core.Response
import core.exceptionhandling.ExceptionHandlingStrategy
import java.time.Instant
import java.time.temporal.ChronoUnit

fun createMockRequest(withActionCode: Int? = null): Request {
    return object : Request {
        override val actionCode = withActionCode ?: -1
        override val exceptionHandlingStrategy: ExceptionHandlingStrategy
            get() = TODO("Not yet implemented")
    }
}

fun createMockResponse(): Response {
    return object : Response {}
}

val Int.minutesAgo: Instant
    get() = Instant.now().minus(this.toLong(), ChronoUnit.MINUTES)