package eu.jrie.put.piper.piperhomeservice.infra.client

import kotlinx.coroutines.flow.asFlow
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class IntelligenceCoreServiceClient {
    fun getSequence(modelId: String, event: String, limit: Int) = List(limit) { "${random()}_$event" } .asFlow()
            .also { logger.info("request for ?modelId=$modelId&event=$event&limit=$limit") }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(IntelligenceCoreServiceClient::class.java)

        fun random() = (1..10_000).random()
    }
}
