package app.di

import io.mockk.mockk
import io.mockk.mockkClass
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import org.koin.test.junit5.mock.MockProviderExtension

class CheckAppModules : KoinTest {
    @JvmField
    @RegisterExtension
    val mockProvider = MockProviderExtension.create { clazz ->
        mockkClass(clazz)
    }

// FIXME
/*
    @Test
    fun checkAllModules() = checkModules {
        modules(appModules)
    }
*/
}