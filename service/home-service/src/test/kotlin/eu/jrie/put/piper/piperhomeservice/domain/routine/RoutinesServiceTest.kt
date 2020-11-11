package eu.jrie.put.piper.piperhomeservice.domain.routine

import eu.jrie.put.piper.piperhomeservice.domain.house.Consents
import eu.jrie.put.piper.piperhomeservice.domain.house.House
import eu.jrie.put.piper.piperhomeservice.domain.house.HousesService
import eu.jrie.put.piper.piperhomeservice.domain.model.ModelService
import eu.jrie.put.piper.piperhomeservice.domain.user.User
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
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import reactor.core.publisher.Mono.just

internal class RoutinesServiceTest {

    private val housesService: HousesService = mockk()
    private val modelService: ModelService = mockk()
    private val intelligenceClient: IntelligenceCoreServiceClient = mockk()

    private val service = RoutinesService(mockk(), mockk(), housesService, modelService, intelligenceClient)

    @Test
    @FlowPreview
    fun `should get continuation suggestions for selected event`() = runBlocking {
        // given
        val modelId = nextUUID

        val expectedMlEvent = "${DEVICE_ID}_$EVENT_ID"
        val (device1, event1) = nextUUID to nextUUID
        val (device2, event2) = nextUUID to nextUUID
        val expectedSuggestions = listOf("${device1}_$event1", "${device2}_$event2")

        every { housesService.checkIsEventOfDevice(DEVICE_ID, EVENT_ID, user) } returns empty()
        every { modelService.getLatestModel(user) } returns just(modelId)
        every { intelligenceClient.getSequence(modelId, expectedMlEvent, N) } returns expectedSuggestions.asFlow()

        // when
        val result = service.getContinuationSuggestions(start, N, user).toList()

        // then
        verifyOrder {
            housesService.checkIsEventOfDevice(DEVICE_ID, EVENT_ID, user)
            modelService.getLatestModel(user)
            intelligenceClient.getSequence(modelId, expectedMlEvent, N)
        }

        val expectedResult = listOf(RoutineEvent(device1, event1), RoutineEvent(device2, event2))
        assertIterableEquals(expectedResult, result)

    }

    @Test
    @FlowPreview
    fun `should throw PredictionsNotAvailableException when house has no model`() = runBlocking {
        // given
        every { housesService.checkIsEventOfDevice(DEVICE_ID, EVENT_ID, user) } returns empty()
        every { modelService.getLatestModel(user) } returns empty()

        // when
        val result = runCatching { service.getContinuationSuggestions(start, N, user).single() }

        // then
        verifyOrder {
            housesService.checkIsEventOfDevice(DEVICE_ID, EVENT_ID, user)
            modelService.getLatestModel(user)
        }

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()!! is PredictionsNotAvailableException)
    }

    private companion object {
        val DEVICE_ID = nextUUID
        val EVENT_ID = nextUUID
        const val N = 5

        val start = RoutineEvent(DEVICE_ID, EVENT_ID)
        val user = User("id", "login", "secret", "house-id", emptySet())
    }
}