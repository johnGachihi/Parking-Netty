import app.actionMap
import app.di.appModules
import org.koin.core.context.GlobalContext.startKoin
import server.Server
import router.EndpointFactoryImpl

fun main() {
    startKoin {
        modules(appModules)
    }

    val endpointFactory = EndpointFactoryImpl(actionMap)
    Server(
        address = "192.168.1.2",
        port = 55123,
        endpointFactory = endpointFactory
    ).start()


//    val modbusRequestHandler = ModbusRequestHandler(endpointFactory)
    /*Server(
//        address = "192.168.1.2",
        port = 55123,
        requestHandler = modbusRequestHandler,
        requestCodec = ModbusRequestCodec(),
        protocolPayloadType = ModbusTcpPayload::class.java,
        protocolCodec = ModbusTcpCodec(ModbusResponseEncoder(), ModbusRequestDecoder())
    ).start()*/
}