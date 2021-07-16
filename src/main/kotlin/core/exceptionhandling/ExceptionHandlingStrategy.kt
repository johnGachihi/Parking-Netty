package core.exceptionhandling

import core.Request
import core.Response

interface ExceptionHandlingStrategy {
    fun handleException(exception: Exception, request: Request): Response
}