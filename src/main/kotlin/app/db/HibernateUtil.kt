package app.db

import app.entities.*
import app.entities.visit.FinishedVisit
import app.entities.visit.OngoingVisit
import app.entities.visit.Visit
import org.hibernate.Session
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
        .addAnnotatedClass(Config::class.java)
        .addAnnotatedClass(ParkingFeeConfig::class.java)
        .addAnnotatedClass(ParkingTariff::class.java)
        .buildMetadata()
    return metadata.buildSessionFactory()
}

object HibernateUtil {
    val sessionFactory: SessionFactory = createHibernateSessionFactory()

    val currentSession: Session
        get() = sessionFactory.currentSession
}