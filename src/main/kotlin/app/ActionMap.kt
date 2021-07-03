package app

import app.endpoints.EntryEndpoint
import app.endpoints.RfidEndpoint

// TODO: Remove
@Deprecated("Will be removed along with EndpointFactoryImpl in favour of `actionMap1` below and KoinEndpointFactory")
val actionMap = mapOf(
    40001 to RfidEndpoint()
)

val actionMap1 = mapOf(
    1 to EntryEndpoint::class,
    40001 to RfidEndpoint::class
)