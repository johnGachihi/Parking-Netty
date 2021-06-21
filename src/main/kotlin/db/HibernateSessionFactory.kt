package db

import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistry
import org.hibernate.boot.registry.StandardServiceRegistryBuilder

interface HibernateSessionFactory {
    val session: Session
}

object HibernateSessionFactoryImpl : HibernateSessionFactory {
    private val sessionFactory: SessionFactory

    init {
        val standardRegistry: StandardServiceRegistry = StandardServiceRegistryBuilder()
            .build()
        val metadata = MetadataSources(standardRegistry)
            .buildMetadata()
        sessionFactory = metadata.buildSessionFactory()
    }

    init { println("${this.javaClass.name} initialized.") }

    override val session: Session
        get() = sessionFactory.openSession()
}