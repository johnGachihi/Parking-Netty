package exceptionhandling

enum class ResponseExceptionStatus {
    INVALID_DATA,
}

class ResponseException(
    val status: ResponseExceptionStatus,
    val responseMessage: String,
    val exception: Exception? = null
) : Exception()