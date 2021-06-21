package di

import db.HibernateSessionFactory
import db.HibernateSessionFactoryImpl
import org.koin.dsl.module


val appModules = module {
    single<HibernateSessionFactory> { HibernateSessionFactoryImpl }
}