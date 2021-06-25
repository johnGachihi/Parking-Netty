import core.Request
import core.Response

fun createMockRequest(withActionCode: Int? = null): Request {
    return object : Request {
        override val actionCode = withActionCode ?: -1
    }
}

fun createMockResponse(): Response {
    return object : Response {}
}