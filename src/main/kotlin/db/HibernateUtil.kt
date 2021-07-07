package db

import app.entities.FinishedVisit
import app.entities.OngoingVisit
import app.entities.Payment
import app.entities.Visit
import org.hibernate.SessionFactory
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistry
import org.hibernate.boot.registry.StandardServiceRegistryBuilder

fun createHibernateSessionFactory(): SessionFactory {
    val standardRegistry: StandardServiceRegistry = StandardServiceRegistryBuilder()
        .build()
    val metadata = MetadataSources(standardRegistry)
        .addAnnotatedClass(Visit::class.java)
        .addAnnotatedClass(OngoingVisit::class.java)
        .addAnnotatedClass(FinishedVisit::class.java)
        .addAnnotatedClass(Payment::class.java)
        .buildMetadata()
    return metadata.buildSessionFactory()
}