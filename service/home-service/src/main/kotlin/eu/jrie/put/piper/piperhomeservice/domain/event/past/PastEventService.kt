package eu.jrie.put.piper.piperhomeservice.domain.event.past

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

@Service
class PastEventService (
        private val repository: PastEventRepository
) {
    suspend fun add(events: Flow<PastEvent>) {
        repository.insert(events).collect()
    }

    fun countEventsAfter(time: Instant, houseId: String): Flow<Int> {
        return flowOf(100)
    }
}
