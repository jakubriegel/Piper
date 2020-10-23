package eu.jrie.put.piper.piperhomeservice.domain.house

data class Room (
        val id: String,
        val houseId: String,
        val name: String
)

data class Device (
        val id: String,
        val roomId: String,
        val typeId: String,
        val name: String
)

data class DeviceType (
        val id: String,
        val houseId: String,
        val name: String,
        val events: Set<DeviceEvent>
)

data class DeviceEvent (
        val id: String,
        val name: String
)
