package eu.jrie.put.piper.piperhomeservice.domain.routine

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.springframework.stereotype.Service
import java.util.*
import java.util.UUID.randomUUID

@Service
class RoutinesService {
    fun routinesForHouse(houseId: String): Flow<Routine> {
        return flow {
            repeat((5..10).random()) {
                emit(Routine("abc123", houseId, emptyList(), randomUUID().toString()))
            }
        }
    }
}
