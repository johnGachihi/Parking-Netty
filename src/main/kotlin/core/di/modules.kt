package core.di

import app.actionMap
import core.exceptionhandling.ExceptionHandler
import core.exceptionhandling.ExceptionHandlerImpl
import core.router.EndpointFactory
import core.router.KoinEndpointFactory
import core.requesthandling.RequestHandlerImpl
import db.HibernateSessionContextManager
import db.HibernateSessionContextManagerImpl
import org.koin.dsl.module
import server.RequestHandler

val coreModules = module {
     // Should this be here
    single<RequestHandler> { RequestHandlerImpl(get(), get(), get()) }
    single<EndpointFactory> { KoinEndpointFactory(actionMap) }
    single<HibernateSessionContextManager> { HibernateSessionContextManagerImpl } // TODO: Unwelcome dependency
    single<ExceptionHandler> { ExceptionHandlerImpl() }
}