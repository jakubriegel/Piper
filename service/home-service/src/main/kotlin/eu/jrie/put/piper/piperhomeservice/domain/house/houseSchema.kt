package eu.jrie.put.piper.piperhomeservice.domain.house

interface HouseNode {
    val id: String
    val name: String
}

data class Room (
        override val id: String,
        val houseId: String,
        override val name: String
) : HouseNode

data class Device (
        override val id: String,
        val roomId: String,
        override val name: String,
        val events: Set<DeviceEvent>
) : HouseNode

data class DeviceEvent(
        val name: String
)
