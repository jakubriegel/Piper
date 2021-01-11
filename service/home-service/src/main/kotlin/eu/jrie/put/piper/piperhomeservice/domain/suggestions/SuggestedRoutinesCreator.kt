package eu.jrie.put.piper.piperhomeservice.domain.suggestions

import eu.jrie.put.piper.piperhomeservice.domain.house.Device
import eu.jrie.put.piper.piperhomeservice.domain.house.DeviceEvent
import eu.jrie.put.piper.piperhomeservice.domain.house.HousesService
import eu.jrie.put.piper.piperhomeservice.domain.routine.RoutineEvent
import eu.jrie.put.piper.piperhomeservice.domain.user.User
import eu.jrie.put.piper.piperhomeservice.infra.common.component1
import eu.jrie.put.piper.piperhomeservice.infra.common.component2
import eu.jrie.put.piper.piperhomeservice.infra.common.nextUUID
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.asFlux
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import reactor.core.publisher.Mono.zip
import reactor.kotlin.core.publisher.toFlux

@FlowPreview
@Component
class SuggestedRoutinesCreator (
        private val repository: SuggestedRoutinesRepository,
        private val housesService: HousesService,
        private val suggestionsService: SuggestionsService
) {
    fun createSuggestedRoutines(houseId: String): Mono<Void> {
        logger.info("Creating suggested routines for house: $houseId")
        val user = User(nextUUID, "", "", houseId, emptySet())
        val devicesMono = housesService.getDevices(user).asFlux().collectList()
        val typesMono = housesService.getDevicesTypes(user)
                .map { (type, events) -> type.id to events.toList() }
                .asFlux().collectList()
        return repository.deleteAllByHouseId(houseId)
            .then(zip(devicesMono, typesMono))
            .map { (devices, types) -> devices to mapOf(*types.toTypedArray()) }
            .flatMapMany { (devices, events) -> List(15) { randomEvent(devices, events) }.toFlux() }
            .distinct()
            .flatMap { head ->
                val n = (2..5).random()
                logger.info("Getting suggestions for $head $n $houseId")
                suggestionsService.getContinuationSuggestions(head, n, user)
                    .asFlux().collectList()
                    .onErrorResume { empty() }
            }
            .map { items -> SuggestedRoutine(nextUUID, houseId, items) }
            .map { logger.info("Created routine $it"); it }
            .flatMap { repository.insert(it) }
            .collectList()
            .flatMap { empty() }
    }

    private companion object {
        fun randomEvent(devices: List<Device>, events: Map<String, List<DeviceEvent>>): RoutineEvent {
            val device = devices.random()
            val event = events.getValue(device.typeId).random()
            return RoutineEvent(device.id, event.id)
        }

        val logger: Logger = LoggerFactory.getLogger(SuggestedRoutinesCreator::class.java)
    }
}
