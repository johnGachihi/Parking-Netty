package core

import exceptionhandling.ExceptionHandlingStrategy

interface Request {
    val actionCode: Int
    val exceptionHandlingStrategy: ExceptionHandlingStrategy
}