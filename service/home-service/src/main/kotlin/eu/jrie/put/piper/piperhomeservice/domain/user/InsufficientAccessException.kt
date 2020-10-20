package eu.jrie.put.piper.piperhomeservice.domain.user

import eu.jrie.put.piper.piperhomeservice.infra.exception.PiperException

data class InsufficientAccessException (
     val resource: String,
     val id: String? = null
) : PiperException("Current user has no access for selected $resource.") {
    override val details = mapOf("resource" to resource, "id" to id)
}
