package eu.jrie.put.piper.piperhomeservice.domain.event.past

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class PastEventService (
        private val repository: PastEventRepository
) {
    suspend fun add(events: Flow<PastEvent>) {
        repository.insert(events).collect()
    }
}
