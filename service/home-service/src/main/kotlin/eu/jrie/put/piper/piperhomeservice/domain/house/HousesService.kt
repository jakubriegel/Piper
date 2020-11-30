package eu.jrie.put.piper.piperhomeservice.domain.house

import eu.jrie.put.piper.piperhomeservice.domain.user.User
import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Mono

interface HousesService {
    fun getHouse(user: User): Mono<House>
    fun createHouse(): Mono<Triple<House, User, User>>
    suspend fun updateSchema(schema: NewHouseSchema, user: User): Mono<Void>

    fun getRooms(user: User): Flow<Room>

    fun getDevice(deviceId: String, user: User): Mono<Device>
    fun getDevices(user: User): Flow<Device>
    fun getDevices(roomId: String, user: User): Flow<Device>

    fun getDeviceType(deviceTypeId: String, user: User): Flow<Pair<DeviceType, Flow<DeviceEvent>>>
    fun getDevicesTypes(user: User): Flow<Pair<DeviceType, Flow<DeviceEvent>>>

    fun getEvent(eventId: String, user: User): Mono<DeviceEvent>
    fun checkIsEventOfDevice(deviceId: String, eventId: String, user: User): Mono<Void>
}

interface HousesServiceConsents {
    fun getHousesIdsWithLearningConsent(): Flow<String>
}
