package endpoints

import core.WriteRequest

class EntryEndpoint : WriteRequestEndpoint {
    override fun handleRequest(writeRequest: WriteRequest) {
        println(writeRequest.data)
    }
}