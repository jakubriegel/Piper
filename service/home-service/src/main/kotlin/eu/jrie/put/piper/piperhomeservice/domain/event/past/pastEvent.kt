package eu.jrie.put.piper.piperhomeservice.domain.event.past

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import eu.jrie.put.piper.piperhomeservice.domain.event.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono
import java.time.Instant

@Document
@JsonInclude()
data class PastEvent (
        @Id
        val id: String?,
        val houseId: String,
        override val deviceId: String,
        override val eventId: String,
        val time: Instant
) : Event {
        constructor(houseId: String, deviceId: String, eventId: String, time: Instant)
                : this(null, houseId, deviceId, eventId, time)
}

interface PastEventRepository : ReactiveMongoRepository<PastEvent, String> {
        fun insert(events: Flow<PastEvent>): Flow<PastEvent> = insert(events.asFlux()).asFlow()
        fun countAllByHouseIdAndTimeGreaterThan(houseId: String, minTime: Instant): Mono<Long>
        fun findAllByHouseIdAndTimeGreaterThan(houseId: String, minTime: Instant): Flow<PastEvent>
}

data class PastEventRow (
        val time: Long,
        val deviceId: String,
        val eventId: String
)

val pastEventCsvSchema: CsvSchema = CsvSchema.builder()
        .addColumn("time")
        .addColumn("deviceId")
        .addColumn("eventId")
        .disableQuoteChar()
        .setLineSeparator('\n')
        .build()
