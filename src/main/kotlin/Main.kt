import app.di.appModules
import core.di.coreModules
import org.koin.core.context.GlobalContext.startKoin
import app.db.HibernateSessionContextManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import server.RequestHandler
import server.Server

class Application : KoinComponent {
    private val requestHandler: RequestHandler by inject()
    private val hibernateSessionContextManager: HibernateSessionContextManager by inject()

    fun run() {
        requestHandler.eventManager.subscribe(hibernateSessionContextManager)

        Server(
            address = "192.168.1.2",
            port = 55123,
            requestHandler = requestHandler
        ).start()
    }
}

// TODO: use KoinComponent
fun main() {
    startKoin { modules(coreModules, appModules) }

    Application().run()
}