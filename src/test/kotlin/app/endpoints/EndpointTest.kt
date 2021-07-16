package app.endpoints

import com.digitalpetri.modbus.ExceptionCode
import com.digitalpetri.modbus.responses.ExceptionResponse
import modbus.ModbusResponse
import db.HibernateSessionContextManager
import app.di.appModules
import core.di.coreModules
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
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(MockKExtension::class)
open class EndpointTest : KoinTest {
    @RegisterExtension
    @JvmField
    val koinTestExtension = KoinTestExtension.create {
        modules(coreModules, appModules, module {
            single<HibernateSessionContextManager> { mockk(relaxed = true) }
            factory { MockServer(get()) }

            // Required here only because in coreModules (appModules for now)
            // a Session is set to be provided from HibernateSessionContextManager.getCurrentSession
            // This will be changed.
            single<Session> { mockk(relaxed = true) }
        })
    }

    @RegisterExtension
    @JvmField
    val koinMockProvider = MockProviderExtension.create {
        mockkClass(it, relaxed = true)
    }

    private val mockServer: MockServer by inject()

    protected fun sendModbusWriteRequest(address: Int, data: ByteBuf): ModbusResponseAssertions {
        val response = mockServer.sendModbusWriteRequest(address, data)
        return ModbusResponseAssertions(response as ModbusResponse)
    }

    class ModbusResponseAssertions(private val response: ModbusResponse) {
        fun assertOk() {
            assertFalse(isExceptional(), "Expected Modbus Response to be Ok but is exceptional.")
        }

        fun assertExceptional(exceptionCode: ExceptionCode? = null) {
            assertTrue(isExceptional(), "Expected Modbus Response to be exceptional but is OK.")

            if (exceptionCode != null) {
                val pdu = response.modbusTcpPayload.modbusPdu as ExceptionResponse
                assertEquals(exceptionCode, pdu.exceptionCode)
            }
        }

        private fun isExceptional(): Boolean {
            return response.modbusTcpPayload.modbusPdu is ExceptionResponse
        }
    }
}