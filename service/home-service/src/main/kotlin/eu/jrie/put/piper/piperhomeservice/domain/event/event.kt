package eu.jrie.put.piper.piperhomeservice.domain.event

interface Event {
    val trigger: String
    val action: String
}
