package eu.jrie.put.piper.piperhomeservice.domain.routine

import eu.jrie.put.piper.piperhomeservice.domain.house.HousesService
import eu.jrie.put.piper.piperhomeservice.domain.model.ModelService
import eu.jrie.put.piper.piperhomeservice.domain.user.AuthService
import eu.jrie.put.piper.piperhomeservice.domain.user.User
import eu.jrie.put.piper.piperhomeservice.infra.client.IntelligenceCoreServiceClient
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

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

    private companion object {
        fun Routine.updateWith(updated: Routine) = Routine(
                id, updated.name, houseId, updated.enabled, updated.events, updated.configuration
        )
    }
}
