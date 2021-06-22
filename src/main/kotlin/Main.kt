import app.actionMap
import di.appModules
import org.koin.core.context.GlobalContext.startKoin
import router.EndpointFactoryImpl
import server.Server

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
}