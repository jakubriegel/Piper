package eu.jrie.put.piper.piperhomeservice.domain.routine

import eu.jrie.put.piper.piperhomeservice.domain.user.AuthService
import eu.jrie.put.piper.piperhomeservice.domain.user.User
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RoutinesService (
        private val repository: RoutinesRepository,
        private val authService: AuthService
) {
    fun routinesForHouse(houseId: String): Flow<RoutinePreview> = repository.findRoutinesPreview(houseId)

    fun routineById(id: String, user: User): Mono<Routine> = repository.findById(id)
                .map { authService.checkForRoutineAccess(user, it) }

    fun createRoutine(routine: Routine) = repository.insert(routine)

    fun updateRoutine(updated: Routine, user: User) = routineById(updated.id, user)
            .map { it.updateWith(updated) }
            .flatMap { repository.save(it) }

    fun deleteRoutine(id: String, user: User) = routineById(id, user)
            .flatMap { repository.deleteById(it.id) }

    fun enableRoutine(id: String, user: User) = switchRoutine(true, id, user)

    fun disableRoutine(id: String, user: User) = switchRoutine(false, id, user)

    private fun switchRoutine(enabled: Boolean, id: String, user: User) = routineById(id, user)
        .map { Routine(id, it.name, it.houseId, enabled, it.events, it.configuration) }
        .flatMap { updateRoutine(it, user) }
        .then()

    private companion object {
        fun Routine.updateWith(updated: Routine) = Routine(
                id, updated.name, houseId, updated.enabled, updated.events, updated.configuration
        )
    }
}
