package db

import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.context.internal.ManagedSessionContext

interface HibernateSessionContextManager {
    fun beginSessionContext()
    fun closeSessionContext()
    fun closeSessionContextExceptionally()
    fun getCurrentSession(): Session
}

object HibernateSessionContextManagerImpl : HibernateSessionContextManager {
    private val sessionFactory: SessionFactory = createHibernateSessionFactory()

    init { println("${this.javaClass.name} initialized.") }

    override fun beginSessionContext() {
        val session = sessionFactory.openSession()
        session.transaction.begin()

        ManagedSessionContext.bind(session)
    }

    override fun closeSessionContext() {
        val currentSession = sessionFactory.currentSession
        currentSession.flush()
        currentSession.transaction.commit()
        currentSession.close()

        ManagedSessionContext.unbind(sessionFactory)
    }

    override fun closeSessionContextExceptionally() {
        val currentSession = sessionFactory.currentSession
        currentSession.transaction.rollback()
        currentSession.close()

        ManagedSessionContext.unbind(sessionFactory)
    }

    // TODO: Make `currentSession` variable.
    override fun getCurrentSession(): Session = sessionFactory.currentSession
}