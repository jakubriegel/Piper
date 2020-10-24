package eu.jrie.put.piper.piperhomeservice.domain.house

import eu.jrie.put.piper.piperhomeservice.domain.user.AuthService
import eu.jrie.put.piper.piperhomeservice.domain.user.User
import eu.jrie.put.piper.piperhomeservice.domain.user.UserService
import eu.jrie.put.piper.piperhomeservice.infra.common.component1
import eu.jrie.put.piper.piperhomeservice.infra.common.component2
import eu.jrie.put.piper.piperhomeservice.infra.common.nextUUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.asFlux
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class HousesService (
        private val repository: HousesRepository,
        private val userService: UserService,
        private val authService: AuthService
) {
    fun houseOfUser(user: User) = repository.findById(user.house)

    fun createHouse() = Mono.just(nextUUID)
            .flatMap { houseId ->
                Mono.just(House(houseId, "name", Models(null, emptySet()), Consents()))
                        .flatMap { repository.save(it) }
                        .zipWith(userService.addUser(houseId))
                        .zipWith(userService.addHouse(houseId)) { (house, ownerUser), houseUser ->
                            Triple (house, ownerUser, houseUser)
                        }
            }

    fun roomsOfUsersHouse(user: User): Flow<Room> {
        listOf(1).component1()
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
        return flowOf(
                DeviceType(nextUUID, "LAMP", user.house, setOf(DeviceEvent(nextUUID, "on"), DeviceEvent(nextUUID, "off"))),
                DeviceType(nextUUID, "WINDOW", user.house, setOf(DeviceEvent(nextUUID, "open"), DeviceEvent(nextUUID, "close")))
        )
    }

    fun deviceTypeById(typeId: String, user: User): Mono<DeviceType> {
        return Mono.just(
                DeviceType(typeId, user.house, "LAMP", setOf(DeviceEvent(nextUUID, "on"), DeviceEvent(nextUUID, "off")))
        ).map { authService.checkForDeviceTypeAccess(user, it) }
    }
}
