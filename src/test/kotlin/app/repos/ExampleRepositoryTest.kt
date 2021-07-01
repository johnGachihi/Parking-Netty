package app.repos

import app.entities.Visit
import org.hibernate.Session
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.*

@ExtendWith(HibernateSessionExtension::class)
class ExampleRepositoryTest {
    lateinit var session: Session

    @Test
    fun `test saves visit`() {
        /*
        val id = saveVisit()

        assertInDb<Visit>(id)
        */
        val visit = Visit().apply {
            ticketCode = 123L
        }

        val id = ExampleRepository(session).save(visit)
        session.flush()

        assertNotNull(session.get(Visit::class.java, id))
    }
}

class ExampleRepository(
    private val session: Session
) {
    fun save(visit: Visit): Long {
        return session.save(visit) as Long
    }
}

