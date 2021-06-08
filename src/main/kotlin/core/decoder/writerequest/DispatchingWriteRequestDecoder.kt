package core.decoder.writerequest

import com.digitalpetri.modbus.codec.ModbusTcpPayload
import com.digitalpetri.modbus.requests.WriteMultipleRegistersRequest
import core.UnsupportedActionException
import core.WriteRequest
import core.RequestAction

/*
* Do I need to inject RequestAction?
* If I do:
*  - I can mock it. I can return whatever action I want to return or null
*    without having to know what the contents of the actual RequestAction
*    are.
*    If I do this, I will need to create a e.g., RequestActionDecoder class
*    for getting a RequestAction from a modbus payload. There will be an
*    interface involved too.
*
*  But:
*  - I can pass whatever action I want using decode()'s argument
*    modbusTcpPayload. What about null? For this I will have to
*    depend on RequestAction's implementation. Therefore, I will
*    do so, but only for when I need a null
*    - But:
*      - if I want a null I can pass a negative number as address
*
* Should I inject the RequestAction to WriteRequestDecoder:
* If I do:
*  - When testing I can easily test if appropriate WriteRequestDecoder is
*    called. I can also pass in whatever combination of the map I wish
*
* If I don't:
*  - When testing I cannot easily know which WriteRequestDecoder is called
*
* */

class DispatchingWriteRequestDecoder(
    private val actionDecoderMapper: Map<RequestAction, WriteRequestDecoder>
) : WriteRequestDecoder
{
    override fun decode(modbusTcpPayload: ModbusTcpPayload): WriteRequest {
        val modbusPdu = modbusTcpPayload.modbusPdu as WriteMultipleRegistersRequest
        val action = RequestAction.fromCode(modbusPdu.address)

        val decoder = actionDecoderMapper[action]
            ?: throw UnsupportedActionException(
                "Action ${modbusPdu.address} is not registered in the actionToDecoderMap")

        return decoder.decode(modbusTcpPayload)
    }
}