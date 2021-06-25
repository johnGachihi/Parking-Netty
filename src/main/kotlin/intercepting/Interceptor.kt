package intercepting

import core.Request
import core.Response

interface Interceptor {
    fun interceptRequest(request: Request): Request
    fun interceptResponse(response: Response): Response
}