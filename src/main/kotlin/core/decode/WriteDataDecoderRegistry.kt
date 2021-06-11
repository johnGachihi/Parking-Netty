package core.decode

import core.RequestAction

class WriteDataDecoderRegistry {
    private val writeDataDecoders:  MutableMap<RequestAction, WriteDataDecoder> = mutableMapOf()

    fun register(action: RequestAction, writeDataDecoder: WriteDataDecoder) {
        writeDataDecoders[action] = writeDataDecoder
    }

    fun getDecoder(action: RequestAction): WriteDataDecoder? =
        writeDataDecoders[action]
}