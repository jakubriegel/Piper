package eu.jrie.put.piper.piperhomeservice.infra.repository

import org.springframework.data.mongodb.core.ReactiveMongoTemplate

abstract class CustomRepository (
        protected val template: ReactiveMongoTemplate
) {
    protected fun projectionOf(vararg fields: Pair<String, Any>) = projectionOf(mapOf(*fields))
    private fun projectionOf(fields: Map<String, Any>) = org.bson.Document(fields)
}
