package db

import app.entities.FinishedVisit
import app.entities.OngoingVisit
import app.entities.Visit
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistry
import org.hibernate.boot.registry.StandardServiceRegistryBuilder

interface HibernateSessionFactory {
    fun createSession(): Session
}

object HibernateSessionFactoryImpl : HibernateSessionFactory {
    private val sessionFactory: SessionFactory

    init {
        val standardRegistry: StandardServiceRegistry = StandardServiceRegistryBuilder()
            .build()
        val metadata = MetadataSources(standardRegistry)
            .addAnnotatedClass(Visit::class.java)
            .addAnnotatedClass(OngoingVisit::class.java)
            .addAnnotatedClass(FinishedVisit::class.java)
            .buildMetadata()
        sessionFactory = metadata.buildSessionFactory()
    }

    init { println("${this.javaClass.name} initialized.") }

    override fun createSession(): Session = sessionFactory.openSession()
}