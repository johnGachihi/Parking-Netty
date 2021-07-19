package core.db

import core.requesthandling.RequestHandlerListener
import org.hibernate.SessionFactory
import org.hibernate.context.internal.ManagedSessionContext

class HibernateSessionContextManager(
    private val sessionFactory: SessionFactory
) : RequestHandlerListener {
    override fun onRequestReceived() {
        val session = sessionFactory.openSession()
        session.beginTransaction()

        ManagedSessionContext.bind(session)
    }

    override fun onRequestHandled() {
        val session = sessionFactory.currentSession
        session.flush()
        session.transaction.commit()
        session.close()

        ManagedSessionContext.unbind(sessionFactory)
    }

    override fun onRequestHandledExceptionally() {
        val session = sessionFactory.currentSession
        session.transaction.rollback()
        session.close()

        ManagedSessionContext.unbind(sessionFactory)
    }
}