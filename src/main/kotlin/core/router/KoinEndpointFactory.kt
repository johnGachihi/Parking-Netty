package core.router

import org.koin.core.component.KoinComponent
import org.koin.java.KoinJavaComponent.get
import kotlin.reflect.KClass

class KoinEndpointFactory(
    private val actionToEndpointMap: Map<Int, KClass<out Endpoint>>
) : EndpointFactory, KoinComponent {
    override fun getEndpoint(actionCode: Int): Endpoint {
        val endpointClass = actionToEndpointMap[actionCode]
            ?: throw UnsupportedActionException()
        return get(endpointClass.java)
    }
}