package app.interceptors

import core.Request
import db.HibernateSessionFactory
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class HibernateSessionContextInterceptorTest {
    @RelaxedMockK
    lateinit var hibernateSessionFactory: HibernateSessionFactory

    @InjectMockKs
    lateinit var hibernateSessionContextInterceptor: HibernateSessionContextInterceptor

    @Test
    fun `returns request as it received it`() {
        val expectedRequest: Request = mockk()
        val actualRequest = hibernateSessionContextInterceptor.interceptRequest(expectedRequest)

        assertEquals(expectedRequest, actualRequest)
    }

    @Test
    fun `Creates a new hibernate session`() {
        hibernateSessionContextInterceptor.interceptRequest(mockk())

        verify(exactly = 1) { hibernateSessionFactory.createSession() }
    }
}