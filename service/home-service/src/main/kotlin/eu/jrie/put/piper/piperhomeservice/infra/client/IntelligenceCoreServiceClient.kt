package eu.jrie.put.piper.piperhomeservice.infra.client

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.reactive.asFlow
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.ACCEPT
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.util.UriBuilder
import org.springframework.web.util.UriBuilderFactory
import org.springframework.web.util.UriComponentsBuilder
import reactor.kotlin.core.publisher.toFlux

@Component
class IntelligenceCoreServiceClient (
        private val webClient: WebClient,
        @Value("\${client.intelligence-core-service.host}")
        private val host: String,
        @Value("\${client.intelligence-core-service.port}")
        private val port: String,
) {
    fun getSequence(modelId: String, event: String, limit: Int): Flow<String> {
        logger.info("request for ?modelId=$modelId&event=$event&limit=$limit")
        return webClient.get()
                .uri {
                    it.path("$host:$port/get-sequence")
                            .queryParam("modelId", modelId)
                            .queryParam("event", event)
                            .queryParam("limit", limit)
                            .build()
                }
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono<SuggestionsResponse>()
                .onErrorMap { e -> ServiceNotAvailableException("intelligence-core-service", e) }
                .map { it.sequence }
                .flatMapMany { it.toFlux() }
                .asFlow()
    }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(IntelligenceCoreServiceClient::class.java)

        data class SuggestionsResponse (
                val modelId: String,
                val head: String,
                val sequence: List<String>
        )
    }
}
