package eu.jrie.put.piper.piperhomeservice.domain.routine

import eu.jrie.put.piper.piperhomeservice.domain.house.HousesService
import eu.jrie.put.piper.piperhomeservice.domain.user.AuthService
import eu.jrie.put.piper.piperhomeservice.domain.user.User
import eu.jrie.put.piper.piperhomeservice.infra.client.IntelligenceCoreServiceClient
import eu.jrie.put.piper.piperhomeservice.infra.common.component1
import eu.jrie.put.piper.piperhomeservice.infra.common.component2
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RoutinesService (
        private val repository: RoutinesRepository,
        private val authService: AuthService,
        private val housesService: HousesService,
        private val intelligenceClient: IntelligenceCoreServiceClient
) {
    fun routinesForHouse(houseId: String): Flow<RoutinePreview> = repository.findRoutinesPreview(houseId)

    fun routineById(id: String, user: User): Mono<Routine> = repository.findById(id)
                .map { authService.checkForRoutineAccess(user, it) }

    fun createRoutine(routine: Routine) = repository.insert(routine)

    fun updateRoutine(updated: Routine, user: User) = routineById(updated.id, user)
            .map { it.updateWith(updated) }
            .flatMap { repository.save(it) }

    @FlowPreview
    fun getContinuationSuggestions(start: RoutineEvent, n: Int, user: User) =
            Mono.zip(
                    housesService.getDevice(start.deviceId, user),
                    housesService.getEvent(start.eventId, user)
            ).flatMap { (device, event) ->
                if (device.typeId != event.deviceTypeId) Mono.error(NotDeviceEventException(device, event))
                else Mono.empty<Void>()
            }
                    .then(housesService.getHouse(user))
                    .map { it.models.current?.id ?: throw NoModelException() }
                    .asFlow()
                    .flatMapConcat { getContinuationSuggestions(start, n, it) }

    @FlowPreview
    private fun getContinuationSuggestions(start: RoutineEvent, n: Int, modelId: String) =
            flowOf(start)
                .map { it.asMlEvent() }
                .flatMapConcat { intelligenceClient.getSequence(modelId, it, n) }
                .map { parseEvent(it) }

    private companion object {
        fun Routine.updateWith(updated: Routine) = Routine(
                id, updated.name, houseId, updated.enabled, updated.events, updated.configuration
        )

        const val ML_EVENT_DELIMITER = '_'

        fun RoutineEvent.asMlEvent() = "$deviceId$ML_EVENT_DELIMITER$eventId"

        fun parseEvent(mlEvent: String) = mlEvent.split(ML_EVENT_DELIMITER)
                .let { (trigger, action) -> RoutineEvent(trigger, action) }
    }
}
