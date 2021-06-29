package di

import db.HibernateSessionContextManagerImpl
import org.hibernate.Session
import org.koin.dsl.module


val appModules = module {
    factory<Session> { HibernateSessionContextManagerImpl.getCurrentSession() }
}