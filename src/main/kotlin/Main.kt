import app.actionMap
import app.interceptors.interceptorChain
import di.appModules
import intercepting.InterceptorManagerImpl
import org.koin.core.context.GlobalContext.startKoin
import router.EndpointFactoryImpl
import router.RequestHandlerImpl
import server.Server

fun main() {
    startKoin { modules(appModules) }

    val requestHandler = RequestHandlerImpl(
        EndpointFactoryImpl(actionMap),
        InterceptorManagerImpl(interceptorChain)
    )
    Server(
        address = "192.168.1.2",
        port = 55123,
        requestHandler = requestHandler
    ).start()
}