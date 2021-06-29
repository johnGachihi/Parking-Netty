package di

import db.HibernateSessionFactory
import db.HibernateSessionFactoryImpl
import org.hibernate.Session
import org.koin.dsl.module


val appModules = module {
    factory<Session> { HibernateSessionFactoryImpl.createSession() }
    single<HibernateSessionFactory> { HibernateSessionFactoryImpl }
}