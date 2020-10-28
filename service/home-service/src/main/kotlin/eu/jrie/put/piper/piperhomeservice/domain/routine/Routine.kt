package eu.jrie.put.piper.piperhomeservice.domain.routine

import eu.jrie.put.piper.piperhomeservice.domain.event.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.query.BasicQuery
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import java.time.DayOfWeek
import java.time.OffsetTime
import java.util.UUID.randomUUID

@Document
data class Routine (
        @Id
        val id: String,
        val name: String,
        val houseId: String,
        val enabled: Boolean,
        val events: List<RoutineEvent>,
        val configuration: RoutineConfiguration?
) {
        constructor(
                name: String, houseId: String, enabled: Boolean,
                events: List<RoutineEvent>, configuration: RoutineConfiguration?
        ) : this(randomUUID().toString(), name, houseId, enabled, events, configuration)
}

data class RoutineConfiguration (
        val days: List<DayOfWeek> = emptyList(),
        val start: OffsetTime? = null,
        val end: OffsetTime? = null
)

data class RoutineEvent (
        override val deviceId: String,
        override val eventId: String
) : Event

interface RoutinesRepository : ReactiveMongoRepository<Routine, String>, RoutinesRepositoryCustom {
        fun findAllByHouseId(houseId: String): Flow<Routine>
}

interface RoutinesRepositoryCustom {
        fun findRoutinesPreview(houseId: String): Flow<RoutinePreview>
}

@Repository
class RoutinesRepositoryCustomImpl (
        private val template: ReactiveMongoTemplate
) : RoutinesRepositoryCustom {
        override fun findRoutinesPreview(houseId: String): Flow<RoutinePreview> {
                val projection = mapOf("id" to 1, "name" to 1, "enabled" to 1)
                        .let { org.bson.Document(it) }
                val query = BasicQuery(Criteria.where("houseId").`is`(houseId).criteriaObject, projection)
                return template.find(query, RoutinePreview::class.java, "routine").asFlow()
        }
}

data class RoutinePreview (
        val id: String,
        val name: String,
        val enabled: Boolean
)
