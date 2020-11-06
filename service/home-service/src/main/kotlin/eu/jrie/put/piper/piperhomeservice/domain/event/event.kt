package eu.jrie.put.piper.piperhomeservice.domain.event

interface Event {
    val deviceId: String
    val eventId: String
}
