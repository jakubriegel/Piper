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
                Room(randomUUID().toString(), user.house, "Living Room"),
                Room(randomUUID().toString(), user.house, "Kitchen")
        )
    }

    fun roomDetails(roomId: String, user: User): Mono<Pair<Room, List<Device>>> {
        return flowOf(Room(roomId, user.house, "Living Room"))
                .map { authService.checkForRoomAccess(user, it) }
                .map {
                    val devices = flowOf(
                            Device(randomUUID().toString(), it.id, "light switch", setOf(DeviceEvent("on"), DeviceEvent("off"))),
                            Device(randomUUID().toString(), it.id, "window", setOf(DeviceEvent("open"), DeviceEvent("close")))
                    )
                    it to devices.toList()
                }
                .asFlux()
                .toMono()
    }

}
