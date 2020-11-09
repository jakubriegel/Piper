package eu.jrie.put.piper.piperhomeservice.infra.client

import eu.jrie.put.piper.piperhomeservice.infra.exception.PiperException

abstract class ClientException(
        val service: String,
        error: Throwable,
        message: String
) : PiperException(message) {
    override val details = mapOf(
            "service" to service,
            "error" to (error.message ?: "unknown")
    )
}

class ServiceNotAvailableException(service: String, error: Throwable) : ClientException(
        service, error, "Requested service is currently unavailable."
)
