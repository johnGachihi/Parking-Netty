import app.actionMap
import db.HibernateSessionContextManagerImpl
import di.appModules
import exceptionhandling.ExceptionHandlerImpl
import org.koin.core.context.GlobalContext.startKoin
import router.KoinEndpointFactory
import router.RequestHandlerImpl
import server.Server

// TODO: use KoinComponent
fun main() {
    startKoin { modules(appModules) }

    val requestHandler = RequestHandlerImpl(
        KoinEndpointFactory(actionMap),
        HibernateSessionContextManagerImpl,
        ExceptionHandlerImpl()
    )
    Server(
        address = "192.168.1.2",
        port = 55123,
        requestHandler = requestHandler
    ).start()
}