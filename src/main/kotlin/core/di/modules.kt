package core.di

import app.actionMap
import core.exceptionhandling.ExceptionHandler
import core.exceptionhandling.ExceptionHandlerImpl
import core.requesthandling.RequestHandlerEventManager
import core.router.EndpointFactory
import core.router.KoinEndpointFactory
import core.requesthandling.RequestHandlerImpl
import org.koin.dsl.module
import server.RequestHandler

val coreModules = module {
    single<RequestHandler> { RequestHandlerImpl(get(), get()) }
    single<EndpointFactory> { KoinEndpointFactory(actionMap) }
    single { RequestHandlerEventManager() }
    single<ExceptionHandler> { ExceptionHandlerImpl() }
}