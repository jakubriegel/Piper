package eu.jrie.put.piper.piperhomeservice.domain.routine

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.util.*
import java.util.UUID.randomUUID

@Service
class RoutinesService {
    fun routinesForHouse(houseId: String): Flow<Routine> {
        return flow {
            repeat(3) {
                val enabled = listOf(true, false).random()
                emit(
                        Routine(
                                randomUUID().toString(),
                                houseId,
                                randomUUID().toString(),
                                enabled,
                                emptyList(),
                                RoutineConfiguration(DayOfWeek.values().toList())
                        )
                )
            }
        }
    }
}
