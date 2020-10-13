package eu.jrie.put.piper.piperhomeservice.domain.routine

import eu.jrie.put.piper.piperhomeservice.domain.event.Event
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Routine (
        @Id
        val id: String,
        val houseId: String,
        val events: List<Map<String, String>>,
        val modelId: String
)

data class RoutineEvent (
        override val trigger: String,
        override val action: String
) : Event
