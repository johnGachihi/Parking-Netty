import app.actionMap
import db.HibernateSessionContextManagerImpl
import di.appModules
import org.koin.core.context.GlobalContext.startKoin
import router.EndpointFactoryImpl
import router.ExceptionHandlerImpl
import router.RequestHandlerImpl
import server.Server

fun main() {
    startKoin { modules(appModules) }

    val requestHandler = RequestHandlerImpl(
        EndpointFactoryImpl(actionMap),
        HibernateSessionContextManagerImpl,
        ExceptionHandlerImpl()
    )
    Server(
        address = "192.168.1.2",
        port = 55123,
        requestHandler = requestHandler
    ).start()
}