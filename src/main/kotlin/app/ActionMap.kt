package app

import app.endpoints.EntryEndpoint
import app.endpoints.ExitEndpoint
import app.endpoints.RfidEndpoint

val actionMap = mapOf(
    1 to EntryEndpoint::class,
    2 to ExitEndpoint::class,
    40001 to RfidEndpoint::class
)