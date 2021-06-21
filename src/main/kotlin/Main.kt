import app.actionMap
import app.di.appModules
import app.endpoints.RfidEndpoint
import com.digitalpetri.modbus.codec.*
import modbus.ModbusRequestCodec
import modbus.ModbusRequestHandler
import org.koin.core.context.GlobalContext.startKoin
import system.EndpointFactoryImpl
import system.server.Server

fun main() {
    startKoin {
        modules(appModules)
    }

    val endpointFactory = EndpointFactoryImpl(actionMap)
    val modbusRequestHandler = ModbusRequestHandler(endpointFactory)
    Server(
//        address = "192.168.1.2",
        port = 55123,
        requestHandler = modbusRequestHandler,
        requestCodec = ModbusRequestCodec(),
        protocolPayloadType = ModbusTcpPayload::class.java,
        protocolCodec = ModbusTcpCodec(ModbusResponseEncoder(), ModbusRequestDecoder())
    ).start()
}