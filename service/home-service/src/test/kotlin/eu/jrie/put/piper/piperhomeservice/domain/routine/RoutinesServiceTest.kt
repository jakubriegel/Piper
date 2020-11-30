package eu.jrie.put.piper.piperhomeservice.domain.routine

import eu.jrie.put.piper.piperhomeservice.DEVICE_ID
import eu.jrie.put.piper.piperhomeservice.EVENT_ID
import eu.jrie.put.piper.piperhomeservice.MODEL_ID
import eu.jrie.put.piper.piperhomeservice.USER
import eu.jrie.put.piper.piperhomeservice.domain.house.HousesService
import eu.jrie.put.piper.piperhomeservice.domain.model.Model
import eu.jrie.put.piper.piperhomeservice.domain.model.ModelService
import eu.jrie.put.piper.piperhomeservice.infra.client.IntelligenceCoreServiceClient
import eu.jrie.put.piper.piperhomeservice.infra.common.nextUUID
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono.empty
import reactor.core.publisher.Mono.just
import java.time.Instant.now

internal class RoutinesServiceTest {

    private val housesService: HousesService = mockk()
    private val modelService: ModelService = mockk()
    private val intelligenceClient: IntelligenceCoreServiceClient = mockk()

    private val service = RoutinesService(mockk(), mockk(), housesService, modelService, intelligenceClient)

    @Test
    @FlowPreview
    fun `should get continuation suggestions for selected event`() = runBlocking {
        // given
        val expectedMlEvent = "${DEVICE_ID}_$EVENT_ID"
        val (device1, event1) = nextUUID to nextUUID
        val (device2, event2) = nextUUID to nextUUID
        val expectedSuggestions = listOf("${device1}_$event1", "${device2}_$event2")

        every { housesService.checkIsEventOfDevice(DEVICE_ID, EVENT_ID, USER) } returns empty()
        every { modelService.getLatestModel(USER) } returns just(Model(MODEL_ID, now(), now(), nextUUID))
        every { intelligenceClient.getSequence(MODEL_ID, expectedMlEvent, N) } returns expectedSuggestions.asFlow()

        // when
        val result = service.getContinuationSuggestions(start, N, USER).toList()

        // then
        verifyOrder {
            housesService.checkIsEventOfDevice(DEVICE_ID, EVENT_ID, USER)
            modelService.getLatestModel(USER)
            intelligenceClient.getSequence(MODEL_ID, expectedMlEvent, N)
        }

        val expectedResult = listOf(RoutineEvent(device1, event1), RoutineEvent(device2, event2))
        assertIterableEquals(expectedResult, result)

    }

    @Test
    @FlowPreview
    fun `should throw PredictionsNotAvailableException when house has no model`() = runBlocking {
        // given
        every { housesService.checkIsEventOfDevice(DEVICE_ID, EVENT_ID, USER) } returns empty()
        every { modelService.getLatestModel(USER) } returns empty()

        // when
        val result = runCatching { service.getContinuationSuggestions(start, N, USER).single() }

        // then
        verifyOrder {
            housesService.checkIsEventOfDevice(DEVICE_ID, EVENT_ID, USER)
            modelService.getLatestModel(USER)
        }

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()!! is PredictionsNotAvailableException)
    }

    private companion object {

        const val N = 5

        val start = RoutineEvent(DEVICE_ID, EVENT_ID)
    }
}