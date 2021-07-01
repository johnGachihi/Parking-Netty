package app.repos

import db.createHibernateSessionFactory
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

class HibernateSessionExtension : BeforeEachCallback, AfterEachCallback {
    private lateinit var sessionFactory: SessionFactory
    private lateinit var session: Session

    override fun beforeEach(context: ExtensionContext) {
        initSession()

        injectSession(context.requiredTestInstance)
    }

    override fun afterEach(context: ExtensionContext?) {
        destroySession()
    }

    private fun initSession() {
        sessionFactory = createHibernateSessionFactory()
        session = sessionFactory.openSession()
        session.beginTransaction()
    }

    private fun injectSession(testInstance: Any) {
        testInstance.javaClass.declaredFields.forEach {
            if (it.type.equals(Session::class.java)) {
                it.isAccessible = true
                it.set(testInstance, session)
            }
        }
    }

    private fun destroySession() {
        session.transaction.rollback()
        session.close()
        sessionFactory.close()
    }
}