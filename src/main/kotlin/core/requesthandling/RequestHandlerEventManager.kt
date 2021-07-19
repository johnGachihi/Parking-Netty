package core.requesthandling

class RequestHandlerEventManager {
    private val listeners: MutableList<RequestHandlerListener> = mutableListOf()

    fun subscribe(listener: RequestHandlerListener) {
        listeners += listener
    }

    fun notifyRequestReceived() {
        for (listener in listeners) {
            listener.onRequestReceived()
        }
    }

    fun notifyRequestHandled() {
        for (listener in listeners) {
            listener.onRequestHandled()
        }
    }

    fun notifyRequestHandleExceptionally() {
        for (listener in listeners) {
            listener.onRequestHandledExceptionally()
        }
    }
}