package eu.jrie.put.piper.piperhomeservice.model

data class Routine (
        val houseId: String,
        val events: List<RoutineEvent>,
        val modelId: String
)
