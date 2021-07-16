package core

import core.exceptionhandling.ExceptionHandlingStrategy

interface Request {
    val actionCode: Int
    val exceptionHandlingStrategy: ExceptionHandlingStrategy
}