package eu.jrie.put.piper.piperhomeservice.domain.house

import eu.jrie.put.piper.piperhomeservice.domain.user.AuthService
import eu.jrie.put.piper.piperhomeservice.domain.user.User
import eu.jrie.put.piper.piperhomeservice.domain.user.UserService
import eu.jrie.put.piper.piperhomeservice.infra.common.component1
import eu.jrie.put.piper.piperhomeservice.infra.common.component2
import eu.jrie.put.piper.piperhomeservice.infra.common.nextUUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.asFlux
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
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

    suspend fun updateSchema(schema: NewHouseSchema, user: User): Mono<HouseSchema> {
        deleteHouseSchema(user.house)
        return updateSchema(schema, user.house)
    }

    private fun updateSchema(schema: NewHouseSchema, houseId: String): Mono<HouseSchema> {
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
                .flatMap { (deviceTypes, deviceEvents) ->
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
                            .map { (rooms, devices) -> HouseSchema(deviceTypes, deviceEvents, rooms, devices) }
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

    fun roomsOfUsersHouse(user: User): Flow<Room> {
        return flowOf(
                Room(nextUUID, user.house, "Living Room"),
                Room(nextUUID, user.house, "Kitchen")
        )
    }

    fun roomDetails(roomId: String, user: User): Mono<Pair<Room, List<Device>>> {
        return flowOf(Room(roomId, user.house, "Living Room"))
                .map { authService.checkForRoomAccess(user, it) }
                .map {
                    val devices = flowOf(
                            Device(nextUUID, it.id, nextUUID, "light switch"),
                            Device(nextUUID, it.id, nextUUID, "window")
                    )
                    it to devices.toList()
                }
                .asFlux()
                .toMono()
    }

    fun deviceTypesOfUsersHouse(user: User): Flow<DeviceType> {
        return deviceTypeRepository.findAllByHouseId(user.house)
    }

    fun deviceTypeById(typeId: String, user: User): Mono<DeviceType> {
        return deviceTypeRepository.findById(typeId)
                .map { authService.checkForDeviceTypeAccess(user, it) }
    }

    private companion object {
        fun <A, B> Flux<Tuple2<A, List<B>>>.collectLists() = collectList()
                .map {
                    val a = it.map { (a, _) -> a }.toSet()
                    val b = it.flatMap { (_, b) -> b }.toSet()
                    a to b
                }
    }
}
