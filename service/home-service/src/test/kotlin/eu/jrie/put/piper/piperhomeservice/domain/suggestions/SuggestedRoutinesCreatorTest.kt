package eu.jrie.put.piper.piperhomeservice.domain.suggestions

import eu.jrie.put.piper.piperhomeservice.HOUSE_ID
import eu.jrie.put.piper.piperhomeservice.domain.house.Device
import eu.jrie.put.piper.piperhomeservice.domain.house.DeviceEvent
import eu.jrie.put.piper.piperhomeservice.domain.house.DeviceType
import eu.jrie.put.piper.piperhomeservice.domain.house.HousesService
import eu.jrie.put.piper.piperhomeservice.infra.common.nextUUID
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono.empty
import reactor.core.publisher.Mono.just

@Suppress("ReactiveStreamsUnusedPublisher")
@FlowPreview
internal class SuggestedRoutinesCreatorTest {

    private val suggestedRoutinesRepository: SuggestedRoutinesRepository = mockk()
    private val housesService: HousesService = mockk()
    private val suggestionsService: SuggestionsService = mockk()

    private val suggestedRoutinesCreator = SuggestedRoutinesCreator(
        suggestedRoutinesRepository, housesService, suggestionsService
    )

    @Test
    fun `should create suggested routines`() {
        // given
        val deviceTypeId = nextUUID
        val device = Device(nextUUID, "", deviceTypeId, "")
        val type = DeviceType(deviceTypeId, HOUSE_ID, "") to flowOf(DeviceEvent(nextUUID, deviceTypeId, ""))

        every { housesService.getDevices(match { it.house == HOUSE_ID }) } returns flowOf(device)
        every { housesService.getDevicesTypes(match { it.house == HOUSE_ID }) } returns flowOf(type)
        every { suggestedRoutinesRepository.deleteAllByHouseId(HOUSE_ID) } returns empty()
        every { suggestionsService.getContinuationSuggestions(any(), any(), match { it.house == HOUSE_ID }) } returns emptyFlow()
        every { suggestedRoutinesRepository.insert(match<SuggestedRoutine> { it.houseId == HOUSE_ID}) } returns just(mockk())

        // when
        suggestedRoutinesCreator.createSuggestedRoutines(HOUSE_ID).block()

        // then
        verifyOrder {
            housesService.getDevices(match { it.house == HOUSE_ID })
            housesService.getDevicesTypes(match { it.house == HOUSE_ID })
            suggestedRoutinesRepository.deleteAllByHouseId(HOUSE_ID)
            suggestedRoutinesRepository.insert(match<SuggestedRoutine> { it.houseId == HOUSE_ID})
        }
    }
}
