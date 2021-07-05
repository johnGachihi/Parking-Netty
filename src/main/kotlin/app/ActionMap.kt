package app

import app.endpoints.EntryEndpoint
import app.endpoints.RfidEndpoint

val actionMap = mapOf(
    1 to EntryEndpoint::class,
    40001 to RfidEndpoint::class
)