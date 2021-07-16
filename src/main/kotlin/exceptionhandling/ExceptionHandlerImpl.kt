package exceptionhandling

import core.Request
import core.Response

class ExceptionHandlerImpl : ExceptionHandler {
    override fun handleException(exception: Exception, request: Request): Response {
        return request.exceptionHandlingStrategy.handleException(exception, request)
    }
}