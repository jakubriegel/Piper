package eu.jrie.put.piper.piperhomeservice.domain.model

import eu.jrie.put.piper.piperhomeservice.HOUSE_ID
import eu.jrie.put.piper.piperhomeservice.domain.event.past.PastEventService
import eu.jrie.put.piper.piperhomeservice.domain.house.HousesServiceConsents
import eu.jrie.put.piper.piperhomeservice.infra.common.nextUUID
import io.mockk.MockKMatcherScope
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyAll
import io.mockk.verifyOrder
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import reactor.core.publisher.Mono.just
import reactor.kafka.sender.SenderResult
import java.time.Instant
import java.time.Instant.now
import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.DAYS

internal class ModelServiceTest {

    private val repository: ModelRepository = mockk()
    private val kafka: ReactiveKafkaProducerTemplate<Int, ModelService.NewModelEvent> = mockk()
    private val housesService: HousesServiceConsents = mockk()
    private val pastEventService: PastEventService = mockk()

    private val service = ModelService(repository, kafka, housesService, pastEventService)

    @Test
    @FlowPreview
    fun `should retrain models`() {
        // given
        val lastUpdateTime = now()
        val latestModel = Model(nextUUID, now(), lastUpdateTime, HOUSE_ID)

        every { housesService.getHousesIdsWithLearningConsent() } returns flowOf(HOUSE_ID)
        every { repository.findTopByHouseIdOrderByCreatedAt(HOUSE_ID) } returns just(latestModel)
        every { pastEventService.countEventsAfter(lastUpdateTime, HOUSE_ID) } returns flowOf(1_000L)
        every { pastEventService.getEventsSince(lastUpdateTime, HOUSE_ID) } returns emptyFlow()
        every { kafka.send(TOPIC, ofType(ModelService.NewModelEvent::class)) } returns just(mockk())

        // when
        service.retrainModels()

        // then
        verify {
            housesService.getHousesIdsWithLearningConsent()
            repository.findTopByHouseIdOrderByCreatedAt(HOUSE_ID)
            pastEventService.countEventsAfter(lastUpdateTime, HOUSE_ID)
            pastEventService.getEventsSince(lastUpdateTime, HOUSE_ID)
            kafka.send(TOPIC, match<ModelService.NewModelEvent> { it.path.endsWith(it.modelId) })
        }
    }

    @Test
    @FlowPreview
    fun `should get events from past 30 days when no models are present`() {
        // given
        every { housesService.getHousesIdsWithLearningConsent() } returns flowOf(HOUSE_ID)
        every { repository.findTopByHouseIdOrderByCreatedAt(HOUSE_ID) } returns empty()
        every { pastEventService.countEventsAfter(any(), HOUSE_ID) } returns flowOf(1_000L)
        every { pastEventService.getEventsSince(any(), HOUSE_ID) } returns emptyFlow()
        every { kafka.send(TOPIC, ofType(ModelService.NewModelEvent::class)) } returns just(mockk())

        // when
        service.retrainModels()

        // then
        verify {
            housesService.getHousesIdsWithLearningConsent()
            repository.findTopByHouseIdOrderByCreatedAt(HOUSE_ID)
            pastEventService.countEventsAfter(nowMinus30Days(), HOUSE_ID)
            pastEventService.getEventsSince(nowMinus30Days(), HOUSE_ID)
            kafka.send(TOPIC, match<ModelService.NewModelEvent> { it.path.endsWith(it.modelId) })
        }
    }

    @Test
    @FlowPreview
    fun `should not emit event if threshold is not reached`() {
        // given
        every { housesService.getHousesIdsWithLearningConsent() } returns flowOf(HOUSE_ID)
        every { repository.findTopByHouseIdOrderByCreatedAt(HOUSE_ID) } returns empty()
        every { pastEventService.countEventsAfter(any(), HOUSE_ID) } returns flowOf(0L)

        // when
        service.retrainModels()

        // then
        verify {
            housesService.getHousesIdsWithLearningConsent()
            repository.findTopByHouseIdOrderByCreatedAt(HOUSE_ID)
            pastEventService.countEventsAfter(nowMinus30Days(), HOUSE_ID)
        }
        verify { pastEventService.getEventsSince(any(), any()) wasNot called }
        verify { kafka wasNot called }
    }

    private companion object {
        const val TOPIC = "UserData"

        fun MockKMatcherScope.nowMinus30Days() = match<Instant> {
            val time = now().minus(30, DAYS)
            it.isBefore(time)
        }
    }
}
