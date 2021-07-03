package app.endpoints

import core.modbus.ModbusResponse
import db.HibernateSessionContextManager
import di.appModules
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkClass
import io.netty.buffer.ByteBuf
import org.hibernate.Session
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import org.koin.test.junit5.mock.MockProviderExtension

@ExtendWith(MockKExtension::class)
open class EndpointTest : KoinTest {
    @RegisterExtension
    @JvmField
    val koinTestExtension = KoinTestExtension.create {
        modules(appModules, module {
            single<HibernateSessionContextManager> { mockk(relaxed = true) }
            factory { MockServer(get()) }

            // Required here only because in systemModules (appModules for now)
            // a Session is set to be provided from HibernateSessionContextManager.getCurrentSession
            // This will be changed.
            single<Session> { mockk(relaxed = true) }
        })
    }

    @RegisterExtension
    @JvmField
    val koinMockProvider = MockProviderExtension.create {
        mockkClass(it)
    }

    protected val mockServer: MockServer by inject()

    protected fun sendModbusWriteRequest(address: Int, data: ByteBuf): ModbusResponseAssertions {
        val response = mockServer.sendModbusWriteRequest(address, data)
        return ModbusResponseAssertions(response as ModbusResponse)
    }

    class ModbusResponseAssertions(private val response: ModbusResponse) {
        fun assertOk() {
            TODO("... ")
        }

        fun assertExceptional() {
            TODO("... ")
        }
    }
}