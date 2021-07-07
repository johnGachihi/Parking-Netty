package app.repos

import app.entities.ParkingTariff
import app.utils.Minutes
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(HibernateSessionExtension::class)
internal class HibernateParkingTariffRepoTest {
    private lateinit var session: Session
    private lateinit var hibernateParkingTariffRepo: HibernateParkingTariffRepo

    @BeforeEach
    fun init() {
        hibernateParkingTariffRepo = HibernateParkingTariffRepo(session)
    }

    @Nested
    @DisplayName("Test getAllInAscendingOrder()")
    inner class TestGetAllInAscendingOrder {
        @Test
        fun `When there is no parking tariff data, returns empty list`() {
            assertTrue(
                hibernateParkingTariffRepo.getAllInAscendingOrder().isEmpty()
            )
        }

        @Test
        fun `When there is parking tariff data, then returns it in ascending order of 'upperLimit'`() {
            val idOne = saveParkingTariff(upperLimit = Minutes(1))
            val idTwo = saveParkingTariff(upperLimit = Minutes(3))
            val idThree = saveParkingTariff(upperLimit = Minutes(2))
            session.flush()

            val parkingTariffs = hibernateParkingTariffRepo.getAllInAscendingOrder()

            assertEquals(3, parkingTariffs.size, "Size assertion")
            assertEquals(idOne, parkingTariffs[0].id)
            assertEquals(idThree, parkingTariffs[1].id)
            assertEquals(idTwo, parkingTariffs[2].id)
        }

        @Test
        fun `When first called, then puts result to query cache`() {
            callInSeparateSession {
                HibernateParkingTariffRepo(it).getAllInAscendingOrder()
            }

            assertQueryCachePutCountEquals(1)
        }

        @Test
        fun `Subsequent calls, hit l2 cache`() {
            callInSeparateSession {
                HibernateParkingTariffRepo(it).getAllInAscendingOrder()
            }
            assertQueryCacheHitCountEquals(0)

            callInSeparateSession {
                HibernateParkingTariffRepo(it).getAllInAscendingOrder()
            }
            assertQueryCacheHitCountEquals(1)

            callInSeparateSession {
                HibernateParkingTariffRepo(it).getAllInAscendingOrder()
            }
            assertQueryCacheHitCountEquals(2)
        }
    }

    private fun saveParkingTariff(upperLimit: Minutes): Long {
        return session.save(
            ParkingTariff().apply {
                this.upperLimit = upperLimit
                fee = 10.0
            }
        ) as Long
    }

    private fun callInSeparateSession(
        sessionFactory: SessionFactory = session.sessionFactory,
        block: (session: Session) -> Unit
    ) {
        val session = sessionFactory.openSession()
        session.beginTransaction()
        block(session)
        session.transaction.commit()
        session.close()
    }

    private fun assertQueryCachePutCountEquals(n: Long) {
        val stat = session.sessionFactory.statistics.queryCachePutCount
        assertEquals(n, stat, "Assert query cache put count")
    }

    private fun assertQueryCacheHitCountEquals(n: Long) {
        val stat = session.sessionFactory.statistics.queryCacheHitCount
        assertEquals(n, stat, "Assert query cache hit count")
    }
}