package eu.jrie.put.piper.piperhomeservice.domain.house

import eu.jrie.put.piper.piperhomeservice.domain.user.AuthService
import eu.jrie.put.piper.piperhomeservice.domain.user.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.asFlux
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.UUID.randomUUID

@Service
class HousesService (
        private val repository: HousesRepository,
        private val authService: AuthService
) {
    fun houseOfUser(user: User) = repository.findById(user.house)

    fun roomsOfUsersHouse(user: User): Flow<Room> {
        return flowOf(
                Room(uuid, user.house, "Living Room"),
                Room(uuid, user.house, "Kitchen")
        )
    }

    fun roomDetails(roomId: String, user: User): Mono<Pair<Room, List<Device>>> {
        return flowOf(Room(roomId, user.house, "Living Room"))
                .map { authService.checkForRoomAccess(user, it) }
                .map {
                    val devices = flowOf(
                            Device(uuid, it.id, uuid, "light switch"),
                            Device(uuid, it.id, uuid, "window")
                    )
                    it to devices.toList()
                }
                .asFlux()
                .toMono()
    }

    fun deviceTypesOfUsersHouse(user: User): Flow<DeviceType> {
        return flowOf(
                DeviceType(uuid, "LAMP", user.house, setOf(DeviceEvent(uuid, "on"), DeviceEvent(uuid, "off"))),
                DeviceType(uuid, "WINDOW", user.house, setOf(DeviceEvent(uuid, "open"), DeviceEvent(uuid, "close")))
        )
    }

    fun deviceTypeById(typeId: String, user: User): Mono<DeviceType> {
        return Mono.just(
                DeviceType(typeId, user.house, "LAMP", setOf(DeviceEvent(uuid, "on"), DeviceEvent(uuid, "off")))
        ).map { authService.checkForDeviceTypeAccess(user, it) }
    }

    private companion object {
        val uuid: String
            get() = randomUUID().toString()
    }
}
