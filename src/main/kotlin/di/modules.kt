package di

import app.actionMap
import app.endpoints.EntryEndpoint
import app.endpoints.ExitEndpoint
import app.endpoints.RfidEndpoint
import app.repos.*
import app.services.*
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
    single<EndpointFactory> { KoinEndpointFactory(actionMap) }
    single<HibernateSessionContextManager> { HibernateSessionContextManagerImpl }
    single<ExceptionHandler> { ExceptionHandlerImpl() }

    // App modules. TODO: Separate from system modules
    factory { EntryEndpoint(get()) }
    factory<EntryService> { EntryServiceImpl(get()) }
    factory<VisitRepository> { VisitRepositoryImpl(get()) }

    factory { ExitEndpoint(get()) }
    factory<ExitService> { ExitServiceImpl(get(), get()) }
    factory<PaymentService> { PaymentServiceImpl(get(), get()) }
    factory<ParkingTariffService> { ParkingTariffServiceImpl(get()) }
    factory<ParkingTariffRepo> { HibernateParkingTariffRepo(get()) }
    factory<ParkingFeeConfigRepo> { ParkingFeeConfigRepoImpl(get()) }
    factory<ConfigRepoHelper> { HibernateConfigRepoHelper(get()) }

    single { RfidEndpoint() }
}