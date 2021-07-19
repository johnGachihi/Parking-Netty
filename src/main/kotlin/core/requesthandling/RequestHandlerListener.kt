package core.requesthandling

interface RequestHandlerListener {
    fun onRequestReceived()
    fun onRequestHandled()
    fun onRequestHandledExceptionally()
}