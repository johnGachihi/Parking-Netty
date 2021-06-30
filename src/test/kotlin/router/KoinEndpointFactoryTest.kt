package router

import core.Request
import core.Response
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.junit5.KoinTestExtension

internal class KoinEndpointFactoryTest : KoinTest {
    private lateinit var koinEndpointFactory: KoinEndpointFactory
    private lateinit var mockEndpointOne: MockEndpointOne
    private lateinit var mockEndpointTwo: MockEndpointTwo

    private val actionCodeForMockEndpointOne = 1
    private val actionCodeForMockEndpointTwo = 2
    private val unregisteredActionCode = -1

    @BeforeEach
    fun initEndpointFactory() {
        koinEndpointFactory = KoinEndpointFactory(mapOf(
            actionCodeForMockEndpointOne to MockEndpointOne::class,
            actionCodeForMockEndpointTwo to MockEndpointTwo::class,
        ))

        mockEndpointOne = MockEndpointOne()
        mockEndpointTwo = MockEndpointTwo()
    }

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(
            module {
                single { mockEndpointOne }
                single { mockEndpointTwo }
            }
        )
    }

    @Test
    fun `When action code is missing from actionToEndpoint Map, then throw UnsupportedActionException`() {
        assertThrows(UnsupportedActionException::class.java) {
            koinEndpointFactory.getEndpoint(unregisteredActionCode)
        }
    }

    @Test
    fun `When action code is in actionToEndpoint Map, then return appropriate endpoint instance from Koin`() {
        val endpointOne = koinEndpointFactory.getEndpoint(actionCodeForMockEndpointOne)
        val endpointTwo = koinEndpointFactory.getEndpoint(actionCodeForMockEndpointTwo)

        assertEquals(endpointOne, mockEndpointOne)
        assertEquals(endpointTwo, mockEndpointTwo)
    }
}

class MockEndpointOne : Endpoint {
    override fun handleRequest(request: Request): Response {
        return object : Response {}
    }
}

class MockEndpointTwo : Endpoint {
    override fun handleRequest(request: Request): Response {
        return object : Response {}
    }
}