package app.repos

import org.hibernate.Session


interface ConfigRepoHelper {
    fun getValue(key: String): String?
}

class HibernateConfigRepoHelper(private val session: Session) : ConfigRepoHelper {
    override fun getValue(key: String): String? {
        return session.createQuery(
            "SELECT c.value FROM Config c WHERE c.key = :key"
        ).setParameter("key", key)
            .uniqueResult() as String?
    }
}


