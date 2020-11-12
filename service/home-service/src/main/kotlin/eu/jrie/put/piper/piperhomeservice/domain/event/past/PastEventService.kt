package eu.jrie.put.piper.piperhomeservice.domain.event.past

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactive.asFlow
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

    fun countEventsAfter(time: Instant, houseId: String): Flow<Long> {
        return repository.countAllByHouseIdAndTimeGreaterThan(houseId, time).asFlow()
                .onEach { logger.info("Found $it events since $time for house $houseId") }
    }

    fun getEventsSince(time: Instant, houseId: String): Flow<PastEvent> {
        return emptyFlow()
    }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(PastEventService::class.java)
    }
}
