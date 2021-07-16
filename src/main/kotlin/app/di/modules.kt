package app.di

import app.endpoints.EntryEndpoint
import app.endpoints.ExitEndpoint
import app.endpoints.RfidEndpoint
import app.repos.*
import app.services.*
import db.HibernateSessionContextManagerImpl
import org.koin.dsl.module

val appModules = module {
    factory { HibernateSessionContextManagerImpl.getCurrentSession() }

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