package eu.jrie.put.piper.piperhomeservice.domain.house

import kotlinx.coroutines.flow.Flow
import org.springframework.data.mongodb.repository.DeleteQuery
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

data class NewHouseSchema (
        val deviceTypes: Set<DeviceTypeSchema>,
        val rooms: Set<RoomSchema>
)

data class HouseSchema (
        val deviceTypes: Set<DeviceType>,
        val deviceEvents: Set<DeviceEvent>,
        val rooms: Set<Room>,
        val devices: Set<Device>
)

data class Room (
        val id: String,
        val houseId: String,
        val name: String
)

interface RoomRepository : ReactiveMongoRepository<Room, String> {
    fun findAllByHouseId(houseId: String): Flow<Room>
    @DeleteQuery fun deleteAllByHouseId(houseId: String): Mono<Void>
}

data class RoomSchema (
        val name: String,
        val devices: Set<DeviceSchema>
)

data class Device (
        val id: String,
        val roomId: String,
        val typeId: String,
        val name: String
)

interface DeviceRepository : ReactiveMongoRepository<Device, String> {
    fun findAllByRoomId(roomId: String): Flow<Device>
    @DeleteQuery fun deleteAllByRoomId(houseId: String): Mono<Void>
}

data class DeviceSchema (
        val type: String,
        val name: String
)

data class DeviceType (
        val id: String,
        val houseId: String,
        val name: String
)

data class DeviceTypeSchema (
        val name: String,
        val events: Set<DeviceEventSchema>
)

interface DeviceTypeRepository : ReactiveMongoRepository<DeviceType, String> {
    fun findAllByHouseId(houseId: String): Flow<DeviceType>
    @DeleteQuery fun deleteAllByHouseId(houseId: String): Mono<Void>
}

data class DeviceEvent (
        val id: String,
        val deviceTypeId: String,
        val name: String
)

data class DeviceEventSchema (
        val name: String
)

interface DeviceEventRepository : ReactiveMongoRepository<DeviceEvent, String> {
    fun findAllByDeviceTypeId(deviceTypeId: String): Flow<DeviceEvent>
    @DeleteQuery fun deleteAllByDeviceTypeId(houseId: String): Mono<Void>
}
