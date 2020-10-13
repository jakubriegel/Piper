package eu.jrie.put.piper.piperhomeservice.domain.routine

import eu.jrie.put.piper.piperhomeservice.domain.event.Event
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import java.util.*

@Table("routine")
data class Routine (
        @PrimaryKey
        val id: UUID,
        val houseId: UUID,
        val events: List<Map<String, String>>,
        val modelId: UUID
)

data class RoutineEvent (
        override val trigger: String,
        override val action: String
) : Event
