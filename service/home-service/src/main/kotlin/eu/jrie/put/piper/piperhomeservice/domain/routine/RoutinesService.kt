package eu.jrie.put.piper.piperhomeservice.domain.routine

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class RoutinesService (
        private val repository: RoutinesRepository
) {
    fun routinesForHouse(houseId: String): Flow<Routine> = repository.findAllByHouseId(houseId)
}
