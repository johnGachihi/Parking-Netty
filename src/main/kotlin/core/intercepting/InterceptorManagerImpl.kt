package core.intercepting

import core.Request
import core.Response

class InterceptorManagerImpl(private val interceptorChain: List<Interceptor>) : InterceptorManager {
    override fun interceptRequest(request: Request): Request {
        var req = request
        for (interceptor in interceptorChain) {
            req = interceptor.interceptRequest(req)
        }
        return req
    }

    override fun interceptResponse(response: Response): Response {
        var res = response
        for (interceptor in interceptorChain) {
            res = interceptor.interceptResponse(res)
        }
        return res
    }
}