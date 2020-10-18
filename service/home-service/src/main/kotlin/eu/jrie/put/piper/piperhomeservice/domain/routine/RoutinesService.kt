package eu.jrie.put.piper.piperhomeservice.domain.routine

import eu.jrie.put.piper.piperhomeservice.domain.user.AuthService
import eu.jrie.put.piper.piperhomeservice.domain.user.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RoutinesService (
        private val repository: RoutinesRepository,
        private val authService: AuthService
) {
    fun routinesForHouse(houseId: String): Flow<RoutinePreview> = repository.findRoutinesPreview(houseId)

    fun routineById(id: String, user: User): Mono<Routine> = repository.findById(id)
                .map { authService.checkForHouseAccess(user, it.houseId); it }

    fun createRoutine(routine: Routine) = repository.insert(routine)

    fun updateRoutine(updated: Routine, user: User) = routineById(updated.id, user)
            .map { it.updateWith(updated) }
            .flatMap { repository.save(it) }

    fun getContinuationSuggestions(start: RoutineEvent, n: Int): Flow<RoutineEvent> {
        val random = { (1..1000).random() }
        return List(n) { RoutineEvent("${start.trigger}_${random()}", "${start.action}_${random()}") } .asFlow()
    }

    private companion object {
        fun Routine.updateWith(updated: Routine) = Routine(
                id, updated.name, houseId, updated.enabled, updated.events, updated.configuration
        )
    }
}
