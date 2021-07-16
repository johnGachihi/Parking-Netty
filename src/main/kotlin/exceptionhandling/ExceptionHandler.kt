package exceptionhandling

import core.Request
import core.Response

interface ExceptionHandler {
    fun handleException(exception: Exception, request: Request): Response
}