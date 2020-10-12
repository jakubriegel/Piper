package eu.jrie.put.piper.piperhomeservice.domain.event.past

import com.datastax.oss.driver.api.core.uuid.Uuids
import eu.jrie.put.piper.piperhomeservice.domain.event.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import java.time.Instant
import java.util.*

@Table("past_event")
data class PastEvent (
        @PrimaryKey
        val id: UUID?,
        val houseId: UUID,
        override val trigger: String,
        override val action: String,
        val time: Instant
) : Event {
        constructor(houseId: UUID, trigger: String, action: String, time: Instant)
                : this(Uuids.timeBased(), houseId, trigger, action, time)
}

interface PastEventRepository : ReactiveCassandraRepository<PastEvent, String> {
        fun insert(events: Flow<PastEvent>): Flow<PastEvent> = insert(events.asFlux()).asFlow()
}
