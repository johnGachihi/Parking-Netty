import app.endpoints.RfidEndpoint
import com.digitalpetri.modbus.codec.*
import modbus.ModbusRequestCodec
import modbus.ModbusRequestHandler
import system.EndpointFactoryImpl
import system.server.Server

fun main() {
//    TBT
//    Server(port = 55124).start()

    val endpointFactory = EndpointFactoryImpl(
        mapOf(
            40001 to RfidEndpoint()
        ))
    val modbusRequestHandler = ModbusRequestHandler(endpointFactory)
    Server(
        address = "192.168.1.2",
        port = 55123,
        requestHandler = modbusRequestHandler,
        requestCodec = ModbusRequestCodec(),
        protocolPayloadType = ModbusTcpPayload::class.java,
        protocolCodec = ModbusTcpCodec(ModbusResponseEncoder(), ModbusRequestDecoder())
    ).start()
}