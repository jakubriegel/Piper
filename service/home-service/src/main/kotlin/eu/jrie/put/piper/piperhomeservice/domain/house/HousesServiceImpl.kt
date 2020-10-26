package eu.jrie.put.piper.piperhomeservice.domain.house

import eu.jrie.put.piper.piperhomeservice.domain.user.AuthService
import eu.jrie.put.piper.piperhomeservice.domain.user.User
import eu.jrie.put.piper.piperhomeservice.domain.user.UserService
import eu.jrie.put.piper.piperhomeservice.infra.common.component1
import eu.jrie.put.piper.piperhomeservice.infra.common.component2
import eu.jrie.put.piper.piperhomeservice.infra.common.nextUUID
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.asFlux
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono

@Service
class HousesServiceImpl (
        private val repository: HousesRepository,
        private val deviceTypeRepository: DeviceTypeRepository,
        private val deviceEventRepository: DeviceEventRepository,
        private val deviceRepository: DeviceRepository,
        private val roomRepository: RoomRepository,
        private val userService: UserService,
        private val authService: AuthService
) : HousesService {
    override fun getHouse(user: User) = repository.findById(user.house)

    override fun createHouse(): Mono<Triple<House, User, User>> {
        return House(nextUUID, "name", Models(null, emptySet()), Consents())
                .toMono()
                .flatMap { repository.insert(it) }
                .zipWhen(
                        { Mono.zip(userService.addUser(it.id), userService.addHouse(it.id)) },
                        { house, (ownerUser, houseUser) -> Triple (house, ownerUser, houseUser) }
                )
    }

    override suspend fun updateSchema(schema: NewHouseSchema, user: User): Mono<Void> {
        deleteHouseSchema(user.house)
        return updateSchema(schema, user.house)
    }

    private suspend fun deleteHouseSchema(houseId: String) {
        deviceTypeRepository.findAllByHouseId(houseId)
                .map { deviceEventRepository.deleteAllByDeviceTypeId(it.id) }
                .collect { it.awaitFirstOrNull() }
        deviceTypeRepository.deleteAllByHouseId(houseId).awaitFirstOrNull()

        roomRepository.findAllByHouseId(houseId)
                .map { deviceRepository.deleteAllByRoomId(it.id) }
                .collect { it.awaitFirstOrNull() }
        roomRepository.deleteAllByHouseId(houseId).awaitFirstOrNull()
    }


    private fun updateSchema(schema: NewHouseSchema, houseId: String): Mono<Void> {
        return schema.deviceTypes.toFlux()
                .distinct()
                .map { DeviceType(nextUUID, houseId, it.name) to it.events.toFlux() }
                .map { (type, eventsSchema) -> type to eventsSchema.map { DeviceEvent(nextUUID, type.id, it.name) } }
                .flatMap { (type, events) ->
                    Mono.zip(
                            deviceTypeRepository.insert(type),
                            deviceEventRepository.insert(events).collectList()
                    )
                }
                .map { (deviceType, _) -> deviceType }
                .collectList()
                .flatMap { deviceTypes ->
                    schema.rooms.toFlux()
                            .distinct()
                            .map { Room(nextUUID, houseId, it.name) to it.devices.toFlux() }
                            .map { (room, devicesSchema) ->
                                room to devicesSchema.map { deviceSchema ->
                                    val type = deviceTypes.first { it.name == deviceSchema.type }
                                    Device(nextUUID, room.id, type.id, deviceSchema.name)
                                }
                            }
                            .flatMap { (room, devices) ->
                                Mono.zip(
                                        roomRepository.insert(room),
                                        deviceRepository.insert(devices).collectList()
                                )
                            }
                            .then(Mono.empty())
                }
    }

    override fun getRooms(user: User): Flow<Room> {
        return roomRepository.findAllByHouseId(user.house)
    }

    override fun getDevice(deviceId: String, user: User): Mono<Device> {
        return deviceRepository.findById(deviceId)
                .switchIfEmpty(Mono.error(DeviceNotFoundException(deviceId)))
                .flatMap { device ->
                    roomRepository.findById(device.roomId)
                            .map { authService.checkForRoomAccess(user, it) }
                            .map { device }
                }
    }

    @FlowPreview
    override fun getDevices(user: User): Flow<Device> {
        return getRooms(user).flatMapConcat { deviceRepository.findAllByRoomId(it.id) }
    }

    override fun getDevices(roomId: String, user: User): Flow<Device> {
        return roomRepository.findById(roomId)
                .map { authService.checkForRoomAccess(user, it) }
                .flatMapMany { deviceRepository.findAllByRoomId(roomId).asFlux() }.asFlow()
    }

    override fun getDeviceType(deviceTypeId: String, user: User): Flow<Pair<DeviceType, Flow<DeviceEvent>>> {
        return deviceTypeRepository.findById(deviceTypeId)
                .asFlow()
                .map { authService.checkForDeviceTypeAccess(user, it) }
                .zipWithEvents()
    }

    override fun getDevicesTypes(user: User): Flow<Pair<DeviceType, Flow<DeviceEvent>>> {
        return deviceTypeRepository.findAllByHouseId(user.house)
                .zipWithEvents()
    }

    private fun Flow<DeviceType>.zipWithEvents() = map {
        it to deviceEventRepository.findAllByDeviceTypeId(it.id)
    }

    override fun getEvent(eventId: String, user: User): Mono<DeviceEvent> {
        return deviceEventRepository.findById(eventId)
                .switchIfEmpty(Mono.error(EventNotFoundException(eventId)))
                .flatMap { event ->
                    deviceTypeRepository.findById(event.deviceTypeId)
                            .map { authService.checkForDeviceTypeAccess(user, it) }
                            .map { event }
                }
    }
}
