        package eu.jrie.put.piper.piperhomeservice.domain.model

import eu.jrie.put.piper.piperhomeservice.domain.event.past.PastEventService
import eu.jrie.put.piper.piperhomeservice.domain.house.HousesServiceConsents
import eu.jrie.put.piper.piperhomeservice.domain.user.User
import eu.jrie.put.piper.piperhomeservice.infra.common.nextUUID
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Instant


        @Service
class ModelService (
                private val repository: ModelRepository,
                private val kafka: ReactiveKafkaProducerTemplate<Int, NewModelMessage>,
                private val housesService: HousesServiceConsents,
                private val pastEventService: PastEventService
) {

    @FlowPreview
    @Scheduled(fixedDelay = 3500)
    fun retrainModels() = runBlocking {
        logger.info("Scanning for models to retrain")
        housesService.getHousesIdsWithLearningConsent()
                .map { it to Instant.MIN }
                .flatMapConcat { (houseId, lastUpdateTime) -> pastEventService.countEventsAfter(lastUpdateTime, houseId) }
                .filter { it >= NEW_MODEL_THRESHOLD }
                .map { nextUUID }
                .collect { emitNewModelEvent(it) }
    }

    private suspend fun emitNewModelEvent(modelId: String) {
        NewModelMessage(modelId, "/some/path/to/model")
                .also { logger.info("sending $it") }
                .let { kafka.send("UserData", it) }
                .doOnError { e -> logger.error("Error during emitting event: ", e) }
                .awaitSingle()
    }

    fun getLatestModel(user: User): Mono<String> {
        return repository.findTopByHouseIdOrderByCreatedAt(user.house)
                .map { it.id }
    }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(ModelService::class.java)

        const val NEW_MODEL_THRESHOLD = 1
    }

    data class NewModelMessage (
            val modelId: String,
            val path: String
    )
}
