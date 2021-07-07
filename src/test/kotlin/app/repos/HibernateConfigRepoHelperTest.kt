package app.repos

import app.entities.Config
import org.hibernate.Session
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(HibernateSessionExtension::class)
internal class HibernateConfigRepoHelperTest {
    private lateinit var session: Session
    private lateinit var hibernateConfigRepoHelper: HibernateConfigRepoHelper

    @BeforeEach
    fun init() {
        hibernateConfigRepoHelper = HibernateConfigRepoHelper(session)
    }

    @Test
    fun `When configuration is available, getConfig returns it as it is in DB`() {
        val config = Config().apply {
            key = "a config key"
            value = "a config value"
        }

        session.save(config)
        session.flush()

        val configValue = hibernateConfigRepoHelper.getValue("a config key")

        assertEquals("a config value", configValue)
    }

    @Test
    fun `When configuration is not available, getConfigValue returns null`() {
        val configValue = hibernateConfigRepoHelper.getValue("a config key")

        assertNull(configValue)
    }
}