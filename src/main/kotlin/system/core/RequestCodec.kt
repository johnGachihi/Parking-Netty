package system.core

interface RequestCodec<TO_DECODE> {
    fun decode(protocolMsg: TO_DECODE): Request
    fun encode(response: Response): TO_DECODE
}