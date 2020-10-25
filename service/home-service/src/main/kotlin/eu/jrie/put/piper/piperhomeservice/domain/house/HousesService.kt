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
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.util.function.Tuple2

@Service
class HousesService (
        private val repository: HousesRepository,
        private val deviceTypeRepository: DeviceTypeRepository,
        private val deviceEventRepository: DeviceEventRepository,
        private val deviceRepository: DeviceRepository,
        private val roomRepository: RoomRepository,
        private val userService: UserService,
        private val authService: AuthService
) {
    fun houseOfUser(user: User) = repository.findById(user.house)

    fun createHouse() = Mono.just(nextUUID)
            .flatMap { houseId ->
                Mono.just(House(houseId, "name", Models(null, emptySet()), Consents()))
                        .flatMap { repository.insert(it) }
                        .zipWith(Mono.zip(userService.addUser(houseId), userService.addHouse(houseId))) { house, (ownerUser, houseUser) ->
                            Triple (house, ownerUser, houseUser)
                        }
            }

    suspend fun updateSchema(schema: NewHouseSchema, user: User): Mono<Void> {
        deleteHouseSchema(user.house)
        return updateSchema(schema, user.house)
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
                .collectLists()
                .flatMap { (deviceTypes, _) ->
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
                            .collectLists()
                            .then(Mono.empty())
                }
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

    fun roomsOfUsersHouse(user: User) = roomsOfHouse(user.house)

    private fun roomsOfHouse(houseId: String) = roomRepository.findAllByHouseId(houseId)

    fun roomDetails(roomId: String, user: User): Mono<Pair<Room, Flow<Device>>> {
        return roomRepository.findById(roomId)
                .map { authService.checkForRoomAccess(user, it) }
                .map { it to devicesOfRoom(it.id) }
    }

    @FlowPreview
    fun devicesOfUsersHouse(user: User) = roomsOfUsersHouse(user).flatMapConcat {
        deviceRepository.findAllByRoomId(it.id)
    } .asFlux()

    fun devicesOfRoom(roomId: String, user: User) = roomRepository.findById(roomId)
            .map { authService.checkForRoomAccess(user, it) }
            .flatMapMany { deviceRepository.findAllByRoomId(roomId).asFlux() }

    fun deviceById(deviceId: String, user: User) = deviceRepository.findById(deviceId)
            .flatMap { device ->
                roomRepository.findById(device.roomId)
                        .map { authService.checkForRoomAccess(user, it) }
                        .map { device }
            }

    private fun devicesOfRoom(roomId: String) = deviceRepository.findAllByRoomId(roomId)

    fun deviceTypesOfUsersHouse(user: User) = deviceTypesOfHouse(user.house)

    private fun deviceTypesOfHouse(houseId: String) = deviceTypeRepository.findAllByHouseId(houseId)
            .map { it to deviceEventRepository.findAllByDeviceTypeId(it.id) }


    fun deviceTypeById(typeId: String, user: User) = deviceTypeRepository.findById(typeId)
            .asFlow()
            .map { authService.checkForDeviceTypeAccess(user, it) }
            .map { it to deviceEventRepository.findAllByDeviceTypeId(it.id) }

    private companion object {
        fun <A, B> Flux<Tuple2<A, List<B>>>.collectLists() = collectList()
                .map {
                    val a = it.map { (a, _) -> a }.toSet()
                    val b = it.flatMap { (_, b) -> b }.toSet()
                    a to b
                }
    }
}
