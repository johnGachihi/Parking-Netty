import app.actionMap
import app.di.appModules
import core.di.coreModules
import db.HibernateSessionContextManagerImpl
import core.exceptionhandling.ExceptionHandlerImpl
import org.koin.core.context.GlobalContext.startKoin
import core.router.KoinEndpointFactory
import core.requesthandling.RequestHandlerImpl
import server.Server

// TODO: use KoinComponent
fun main() {
    startKoin { modules(coreModules, appModules) }

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