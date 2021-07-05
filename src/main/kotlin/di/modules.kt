package di

import app.actionMap1
import app.endpoints.EntryEndpoint
import app.endpoints.RfidEndpoint
import app.repos.VisitRepository
import app.repos.VisitRepositoryImpl
import app.services.EntryService
import app.services.EntryServiceImpl
import db.HibernateSessionContextManager
import db.HibernateSessionContextManagerImpl
import org.hibernate.Session
import org.koin.dsl.module
import router.*
import server.RequestHandler


val appModules = module {
    // System modules
    factory<Session> { HibernateSessionContextManagerImpl.getCurrentSession() }
    single<RequestHandler> { RequestHandlerImpl(get(), get(), get()) }
    single<EndpointFactory> { KoinEndpointFactory(actionMap1) }
    single<HibernateSessionContextManager> { HibernateSessionContextManagerImpl }
    single<ExceptionHandler> { ExceptionHandlerImpl() }

    // App modules
    factory { EntryEndpoint(get()) }
    factory<EntryService> { EntryServiceImpl(get()) }
    factory<VisitRepository> { VisitRepositoryImpl(get()) }

    single { RfidEndpoint() }
}