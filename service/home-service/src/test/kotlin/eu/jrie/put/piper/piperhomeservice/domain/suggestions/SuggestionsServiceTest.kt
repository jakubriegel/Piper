package eu.jrie.put.piper.piperhomeservice.domain.suggestions

import eu.jrie.put.piper.piperhomeservice.DEVICE_ID
import eu.jrie.put.piper.piperhomeservice.EVENT_ID
import eu.jrie.put.piper.piperhomeservice.HOUSE_ID
import eu.jrie.put.piper.piperhomeservice.MODEL_ID
import eu.jrie.put.piper.piperhomeservice.USER
import eu.jrie.put.piper.piperhomeservice.domain.house.HousesService
import eu.jrie.put.piper.piperhomeservice.domain.model.Model
import eu.jrie.put.piper.piperhomeservice.domain.model.ModelRepository
import eu.jrie.put.piper.piperhomeservice.domain.routine.PredictionsNotAvailableException
import eu.jrie.put.piper.piperhomeservice.domain.routine.RoutineEvent
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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux.just
import reactor.core.publisher.Mono
import java.time.Instant.now

internal class SuggestionsServiceTest {

    private val suggestedRoutinesRepository: SuggestedRoutinesRepository = mockk()
    private val housesService: HousesService = mockk()
    private val modelRepository: ModelRepository = mockk()
    private val intelligenceClient: IntelligenceCoreServiceClient = mockk()

    private val service = SuggestionsService(suggestedRoutinesRepository, housesService, modelRepository, intelligenceClient)

    @Test
    @FlowPreview
    fun `should get continuation suggestions for selected event`() = runBlocking {
        // given
        val expectedMlEvent = "${DEVICE_ID}_$EVENT_ID"
        val (device1, event1) = nextUUID to nextUUID
        val (device2, event2) = nextUUID to nextUUID
        val providedSuggestions = listOf("${start.deviceId}_${start.eventId}", "${device1}_$event1", "${device2}_$event2")
        val expectedResult = listOf(RoutineEvent(device1, event1), RoutineEvent(device2, event2))

        every { housesService.checkIsEventOfDevice(DEVICE_ID, EVENT_ID, USER) } returns Mono.empty()
        every { modelRepository.findTopByHouseIdOrderByCreatedAt(HOUSE_ID) } returns Mono.just(Model(MODEL_ID, now(), now(), nextUUID))
        every { intelligenceClient.getSequence(MODEL_ID, expectedMlEvent, N) } returns providedSuggestions.asFlow()

        // when
        val result = service.getContinuationSuggestions(start, N, USER).toList()

        // then
        verifyOrder {
            housesService.checkIsEventOfDevice(DEVICE_ID, EVENT_ID, USER)
            modelRepository.findTopByHouseIdOrderByCreatedAt(HOUSE_ID)
            intelligenceClient.getSequence(MODEL_ID, expectedMlEvent, N)
        }

        assertIterableEquals(expectedResult, result)
    }

    @Test
    @FlowPreview
    fun `should throw PredictionsNotAvailableException when house has no model`() = runBlocking {
        // given
        every { housesService.checkIsEventOfDevice(DEVICE_ID, EVENT_ID, USER) } returns Mono.empty()
        every { modelRepository.findTopByHouseIdOrderByCreatedAt(HOUSE_ID) } returns Mono.empty()

        // when
        val result = runCatching { service.getContinuationSuggestions(start, N, USER).single() }

        // then
        verifyOrder {
            housesService.checkIsEventOfDevice(DEVICE_ID, EVENT_ID, USER)
            modelRepository.findTopByHouseIdOrderByCreatedAt(HOUSE_ID)
        }

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()!! is PredictionsNotAvailableException)
    }

    @Test
    fun `should get suggested routines`() {
        // given
        val expectedEvents = listOf(RoutineEvent(nextUUID, nextUUID))
        val expectedRoutine = SuggestedRoutine(nextUUID, HOUSE_ID, expectedEvents)

        every { suggestedRoutinesRepository.findRandom(N, HOUSE_ID) } returns just(expectedRoutine)

        // when
        val result = service.getSuggestedRoutines(N, USER).collectList().block()

        // then
        assertEquals(listOf(expectedEvents), result)
    }

    private companion object {
        const val N = 5
        val start = RoutineEvent(DEVICE_ID, EVENT_ID)
    }
}
