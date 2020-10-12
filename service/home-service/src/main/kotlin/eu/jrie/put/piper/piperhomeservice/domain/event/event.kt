package eu.jrie.put.piper.piperhomeservice.domain.event

import java.time.Instant

interface Event {
    val trigger: String
    val action: String
}

data class RoutineEvent (
        override val trigger: String,
        override val action: String
) : Event
