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
                .map { authService.checkForHouseAccess(user, it.houseId); it }
}
