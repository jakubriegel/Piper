package eu.jrie.put.piper.piperhomeservice.domain.model

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import eu.jrie.put.piper.piperhomeservice.domain.event.past.PastEvent
import eu.jrie.put.piper.piperhomeservice.domain.event.past.PastEventRow
import eu.jrie.put.piper.piperhomeservice.domain.event.past.PastEventService
import eu.jrie.put.piper.piperhomeservice.domain.event.past.pastEventCsvSchema
import eu.jrie.put.piper.piperhomeservice.domain.house.HousesServiceConsents
import eu.jrie.put.piper.piperhomeservice.infra.common.nextUUID
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactive.collect
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.File
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class ModelUpdater (
        private val modelService: ModelService,
        private val kafka: ReactiveKafkaProducerTemplate<Int, NewModelEvent>,
        private val housesService: HousesServiceConsents,
        private val pastEventService: PastEventService,
        @Value("\${models.data-files-dir}")
        dataFilesDirPath: String,
        csvMapper: CsvMapper
) {

    private val dataFilesDir = File(dataFilesDirPath).also { if(!it.exists()) it.mkdir() }
    private val writer = csvMapper.writerFor(PastEventRow::class.java).with(pastEventCsvSchema)

    @FlowPreview
    @Scheduled(fixedDelay = 10_000, initialDelay = 1_000)
    fun retrainModels() = runBlocking {
        logger.info("Scanning for models to retrain")
        housesService.getHousesIdsWithLearningConsent()
                .flatMapConcat { houseId ->
                    modelService.getLatestModel(houseId)
                            .map { houseId to it.createdAt }
                            .defaultIfEmpty(houseId to Instant.now().minus(30, ChronoUnit.DAYS))
                            .asFlow()
                }
                .filter { (houseId, lastUpdateTime) ->
                    val n = pastEventService.countEventsAfter(lastUpdateTime, houseId)
                    n.single() >= NEW_MODEL_THRESHOLD
                }
                .map { (houseId, lastUpdateTime) -> houseId to pastEventService.getEventsSince(lastUpdateTime, houseId) }
                .map { (houseId, dataset) ->
                    val newModelId = nextUUID
                    val dataFilePath = saveDatasetToFile(dataset, newModelId)
                    NotReadyModel(newModelId, Instant.now(), houseId, dataFilePath)
                }
                .onEach { modelService.addNewModel(it) }
                .map { NewModelEvent(it.id, it.dataFilePath) }
                .collect { emitNewModelEvent(it) }
    }

    private suspend fun saveDatasetToFile(dataset: Flow<PastEvent>, modelId: String): String {
        val file = newDataFile(modelId)
        logger.info("Saving dataset to ${file.absolutePath}")
        dataset.asFlux()
                .map { PastEventRow(it.time.epochSecond, it.deviceId, it.eventId) }
                .map { writer.writeValueAsBytes(it) }
                .collect { file.appendBytes(it) }
        return file.absolutePath
    }

    private fun newDataFile(modelId: String) = File("${dataFilesDir.absolutePath}/dataset_$modelId.csv")

    private suspend fun emitNewModelEvent(event: NewModelEvent) {
        logger.info("Emitting new model event: $event")
        kafka.send(NEW_MODEL_EVENTS_TOPIC, event)
                .doOnError { e -> logger.error("Error during emitting event: ", e) }
                .awaitSingle()
    }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(ModelService::class.java)

        const val NEW_MODEL_EVENTS_TOPIC = "UserData"
        const val NEW_MODEL_THRESHOLD = 1
    }
}
