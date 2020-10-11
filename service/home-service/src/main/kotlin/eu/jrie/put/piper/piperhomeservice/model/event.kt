package eu.jrie.put.piper.piperhomeservice.model

import java.time.Instant

interface Event {
    val trigger: String
    val action: String
}

data class PastEvent (
        val houseId: String,
        override val trigger: String,
        override val action: String,
        val time: String
) : Event

data class RoutineEvent (
        override val trigger: String,
        override val action: String
) : Event
