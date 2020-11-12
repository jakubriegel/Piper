        package eu.jrie.put.piper.piperhomeservice.domain.model

import eu.jrie.put.piper.piperhomeservice.domain.event.past.PastEvent
import eu.jrie.put.piper.piperhomeservice.domain.event.past.PastEventService
import eu.jrie.put.piper.piperhomeservice.domain.house.HousesServiceConsents
import eu.jrie.put.piper.piperhomeservice.domain.user.User
import eu.jrie.put.piper.piperhomeservice.infra.common.nextUUID
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Instant.now
import java.time.temporal.ChronoUnit.DAYS

@Service
class ModelService (
        private val repository: ModelRepository,
        private val kafka: ReactiveKafkaProducerTemplate<Int, NewModelEvent>,
        private val housesService: HousesServiceConsents,
        private val pastEventService: PastEventService
) {

    @FlowPreview
    @Scheduled(fixedDelay = 10_000)
    fun retrainModels() = runBlocking {
        logger.info("Scanning for models to retrain")
        housesService.getHousesIdsWithLearningConsent()
                .flatMapConcat { houseId ->
                    getLatestModel(houseId)
                            .map { houseId to it.createdAt }
                            .defaultIfEmpty(houseId to now().minus(30, DAYS))
                            .asFlow()
                }
                .filter { (houseId, lastUpdateTime) ->
                    val n = pastEventService.countEventsAfter(lastUpdateTime, houseId)
                    n.single() >= NEW_MODEL_THRESHOLD
                }
                .map { (houseId, lastUpdateTime) -> pastEventService.getEventsSince(lastUpdateTime, houseId) }
                .map { dataset ->
                    val newModelId = nextUUID
                    val pathToDataFile = saveDatasetToFile(dataset, newModelId)
                    NewModelEvent(newModelId, pathToDataFile)
                }
                .collect { emitNewModelEvent(it) }
    }

    private fun saveDatasetToFile(dataset: Flow<PastEvent>, modelId: String): String = "/some/path/to/model/$modelId"

    private suspend fun emitNewModelEvent(event: NewModelEvent) {
        logger.info("Emitting new model event: $event")
        kafka.send("UserData", event)
                .doOnError { e -> logger.error("Error during emitting event: ", e) }
                .awaitSingle()
    }

    fun getLatestModel(user: User) = getLatestModel(user.house)

    private fun getLatestModel(houseId: String): Mono<Model> {
        return repository.findTopByHouseIdOrderByCreatedAt(houseId)
    }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(ModelService::class.java)

        const val NEW_MODEL_THRESHOLD = 1
    }

    data class NewModelEvent (
            val modelId: String,
            val path: String
    )
}
