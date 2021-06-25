package intercepting

import core.Request
import core.Response

interface InterceptorManager {
    fun interceptRequest(request: Request): Request
    fun interceptResponse(response: Response): Response
}

