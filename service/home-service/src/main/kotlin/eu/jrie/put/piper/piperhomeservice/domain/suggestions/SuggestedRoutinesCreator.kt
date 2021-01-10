package eu.jrie.put.piper.piperhomeservice.domain.suggestions

import eu.jrie.put.piper.piperhomeservice.domain.house.Device
import eu.jrie.put.piper.piperhomeservice.domain.house.DeviceEvent
import eu.jrie.put.piper.piperhomeservice.domain.house.HousesService
import eu.jrie.put.piper.piperhomeservice.domain.routine.RoutineEvent
import eu.jrie.put.piper.piperhomeservice.domain.user.User
import eu.jrie.put.piper.piperhomeservice.infra.common.nextUUID
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.runBlocking
import org.jetbrains.annotations.TestOnly
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import eu.jrie.put.piper.piperhomeservice.infra.common.component1
import eu.jrie.put.piper.piperhomeservice.infra.common.component2
import reactor.core.publisher.Mono.empty
import reactor.kotlin.core.publisher.toFlux

@Component
class SuggestedRoutinesCreator (
        private val repository: SuggestedRoutinesRepository,
        private val housesService: HousesService
) {
    @TestOnly
    fun createSuggestedRoutines(houseId: String): Mono<Void> {
        val user = User(nextUUID, "", "", houseId, emptySet())
        val devicesMono = housesService.getDevices(user).asFlux().collectList()
        val typesMono = housesService.getDevicesTypes(user)
                .map { (type, events) -> type.id to events.toList() }
                .asFlux().collectList()
        return Mono.zip(devicesMono, typesMono)
                .map { (devices, types) -> devices to mapOf(*types.toTypedArray()) }
                .flatMapMany { (devices, events) -> List(15) { devices to events }.toFlux() }
                .flatMap { (devices, events) ->
                    val items = List((2..5).random()) { randomEvent(devices, events) }
                    val routine = SuggestedRoutine(nextUUID, houseId, items)
                    repository.insert(routine)
                }
                .flatMap { empty<Void>() }
                .reduce { a, _ -> a }
    }

    private fun randomEvent(devices: List<Device>, events: Map<String, List<DeviceEvent>>): RoutineEvent {
        val device = devices.random()
        val event = events.getValue(device.typeId).random()
        return RoutineEvent(device.id, event.id)
    }
}
