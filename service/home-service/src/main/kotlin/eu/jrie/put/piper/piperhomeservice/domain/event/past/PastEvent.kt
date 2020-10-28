package eu.jrie.put.piper.piperhomeservice.domain.event.past

import eu.jrie.put.piper.piperhomeservice.domain.event.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import java.time.Instant

@Document
data class PastEvent (
        @Id
        val id: String?,
        val houseId: String,
        override val trigger: String,
        override val action: String,
        val time: Instant
) : Event {
        constructor(houseId: String, trigger: String, action: String, time: Instant)
                : this(null, houseId, trigger, action, time)
}

interface PastEventRepository : ReactiveMongoRepository<PastEvent, String> {
        fun insert(events: Flow<PastEvent>): Flow<PastEvent> = insert(events.asFlux()).asFlow()
}
