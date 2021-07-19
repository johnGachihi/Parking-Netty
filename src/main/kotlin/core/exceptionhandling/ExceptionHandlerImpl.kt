package core.exceptionhandling

import core.Request
import core.Response

class ExceptionHandlerImpl : ExceptionHandler {
    override fun handleException(exception: Exception, request: Request): Response {
        exception.printStackTrace()
        return request.exceptionHandlingStrategy.handleException(exception, request)
    }
}