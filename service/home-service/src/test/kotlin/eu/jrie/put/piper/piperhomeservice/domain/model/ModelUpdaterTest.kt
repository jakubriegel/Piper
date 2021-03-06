package eu.jrie.put.piper.piperhomeservice.domain.model

import eu.jrie.put.piper.piperhomeservice.HOUSE_ID
import eu.jrie.put.piper.piperhomeservice.TEMP_DIR
import eu.jrie.put.piper.piperhomeservice.domain.event.past.PastEvent
import eu.jrie.put.piper.piperhomeservice.domain.event.past.PastEventService
import eu.jrie.put.piper.piperhomeservice.domain.house.HousesServiceConsents
import eu.jrie.put.piper.piperhomeservice.infra.common.isUUID
import eu.jrie.put.piper.piperhomeservice.infra.common.nextUUID
import eu.jrie.put.piper.piperhomeservice.infra.mapper.MapperConfig
import io.mockk.MockKMatcherScope
import io.mockk.called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import reactor.core.publisher.Mono.empty
import reactor.core.publisher.Mono.just
import java.io.File
import java.time.Instant
import java.time.Instant.now
import java.time.temporal.ChronoUnit.DAYS

@Suppress("ReactiveStreamsUnusedPublisher")
internal class ModelUpdaterTest {

    private val modelService: ModelService = mockk()
    private val kafka: ReactiveKafkaProducerTemplate<Int, NewModelEvent> = mockk()
    private val housesService: HousesServiceConsents = mockk()
    private val pastEventService: PastEventService = mockk()
    private val mapper = MapperConfig().csvMapper()

    private val updater = ModelUpdater(
            modelService,
            kafka,
            housesService,
            pastEventService, TEMP_DIR.absolutePath, mapper,
            1
    )

    @Test
    @FlowPreview
    fun `should retrain models`() {
        // given
        val lastUpdateTime = now()
        val latestModel = Model(nextUUID, now(), lastUpdateTime, HOUSE_ID)

        val createdModel = slot<NotReadyModel>()
        val newModelEvent = slot<NewModelEvent>()

        every { housesService.getHousesIdsWithLearningConsent() } returns flowOf(HOUSE_ID)
        every { modelService.getNotReadyModel(HOUSE_ID) } returns empty()
        every { modelService.getLatestModel(HOUSE_ID) } returns just(latestModel)
        every { pastEventService.countEventsAfter(lastUpdateTime, HOUSE_ID) } returns flowOf(1_000L)
        every { pastEventService.getEventsSince(lastUpdateTime, HOUSE_ID) } returns pastEvents.asFlow()
        coEvery { modelService.addNewModel(capture(createdModel)) } just runs
        every { kafka.send(TOPIC, capture(newModelEvent)) } returns just(mockk())

        // when
        updater.retrainModels()

        // then
        coVerify {
            housesService.getHousesIdsWithLearningConsent()
            modelService.getNotReadyModel(HOUSE_ID)
            modelService.getLatestModel(HOUSE_ID)
            pastEventService.countEventsAfter(lastUpdateTime, HOUSE_ID)
            pastEventService.getEventsSince(lastUpdateTime, HOUSE_ID)
            modelService.addNewModel(ofType(NotReadyModel::class))
            kafka.send(TOPIC, match<NewModelEvent> { it.path.contains(it.modelId) })
        }

        // and new model was created
        val newModelId = createdModel.captured.id
        assertTrue(newModelId.isUUID())

        // and dataset saved correctly
        val dataFile = File("${TEMP_DIR.absolutePath}/dataset_$newModelId.csv").readLines()
        assertEquals(pastEvents.size, dataFile.size)
        assertTrue {
            dataFile.zip(pastEvents)
                    .all { (row, event) ->
                        row == "${event.time.epochSecond},${event.deviceId},${event.eventId}"
                    }
        }

        // and event emitted correctly
        val idInEvent = newModelEvent.captured.modelId
        assertEquals(newModelId, idInEvent)
    }

    @Test
    @FlowPreview
    fun `should not retrain models if not ready model is present for house`() {
        // given
        every { housesService.getHousesIdsWithLearningConsent() } returns flowOf(HOUSE_ID)
        every { modelService.getNotReadyModel(HOUSE_ID) } returns just(mockk())

        // when
        updater.retrainModels()

        // then
        verify {
            housesService.getHousesIdsWithLearningConsent()
            modelService.getNotReadyModel(HOUSE_ID)
            pastEventService wasNot called
            kafka wasNot called
        }
    }

    @Test
    @FlowPreview
    fun `should get events from past 30 days when no models are present`() {
        // given
        val createdModel = slot<NotReadyModel>()
        val newModelEvent = slot<NewModelEvent>()

        every { housesService.getHousesIdsWithLearningConsent() } returns flowOf(HOUSE_ID)
        every { modelService.getNotReadyModel(HOUSE_ID) } returns empty()
        every { modelService.getLatestModel(HOUSE_ID) } returns empty()
        every { pastEventService.countEventsAfter(any(), HOUSE_ID) } returns flowOf(1_000L)
        every { pastEventService.getEventsSince(any(), HOUSE_ID) } returns pastEvents.asFlow()
        coEvery { modelService.addNewModel(capture(createdModel)) } just runs
        every { kafka.send(TOPIC, capture(newModelEvent)) } returns just(mockk())

        // when
        updater.retrainModels()

        // then
        coVerify {
            housesService.getHousesIdsWithLearningConsent()
            modelService.getNotReadyModel(HOUSE_ID)
            modelService.getLatestModel(HOUSE_ID)
            pastEventService.countEventsAfter(nowMinus30Days(), HOUSE_ID)
            pastEventService.getEventsSince(nowMinus30Days(), HOUSE_ID)
            modelService.addNewModel(ofType(NotReadyModel::class))
            kafka.send(TOPIC, match<NewModelEvent> { it.path.contains(it.modelId) })
        }

        // and new model was created
        val newModelId = createdModel.captured.id
        assertTrue(newModelId.isUUID())

        // and dataset saved correctly
        val dataFile = File("${TEMP_DIR.absolutePath}/dataset_$newModelId.csv").readLines()
        assertEquals(pastEvents.size, dataFile.size)
        assertTrue {
            dataFile.zip(pastEvents)
                    .all { (row, event) ->
                        row == "${event.time.epochSecond},${event.deviceId},${event.eventId}"
                    }
        }

        // and event emitted correctly
        val idInEvent = newModelEvent.captured.modelId
        assertEquals(newModelId, idInEvent)
    }

    @Test
    @FlowPreview
    fun `should not emit event if threshold is not reached`() {
        // given
        every { housesService.getHousesIdsWithLearningConsent() } returns flowOf(HOUSE_ID)
        every { modelService.getNotReadyModel(HOUSE_ID) } returns empty()
        every { modelService.getLatestModel(HOUSE_ID) } returns empty()
        every { pastEventService.countEventsAfter(any(), HOUSE_ID) } returns flowOf(0L)

        // when
        updater.retrainModels()

        // then
        verify {
            housesService.getHousesIdsWithLearningConsent()
            modelService.getNotReadyModel(HOUSE_ID)
            modelService.getLatestModel(HOUSE_ID)
            pastEventService.countEventsAfter(nowMinus30Days(), HOUSE_ID)
        }
        verify { pastEventService.getEventsSince(any(), any()) wasNot called }
        verify { kafka wasNot called }
    }

    private companion object {
        const val TOPIC = "UserData"
        val pastEvents = listOf(
                PastEvent(nextUUID, HOUSE_ID, nextUUID, nextUUID, now()),
                PastEvent(nextUUID, HOUSE_ID, nextUUID, nextUUID, now())
        )

        fun MockKMatcherScope.nowMinus30Days() = match<Instant> {
            val time = now().minus(30, DAYS)
            it.isBefore(time) || it == time
        }
    }
}
