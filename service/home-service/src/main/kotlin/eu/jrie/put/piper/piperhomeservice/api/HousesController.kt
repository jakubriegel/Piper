package eu.jrie.put.piper.piperhomeservice.api

import eu.jrie.put.piper.piperhomeservice.api.message.ApiResponse
import eu.jrie.put.piper.piperhomeservice.api.message.HouseResponse
import eu.jrie.put.piper.piperhomeservice.api.message.RoomResponse
import eu.jrie.put.piper.piperhomeservice.api.message.RoomsResponse
import eu.jrie.put.piper.piperhomeservice.api.message.asMessage
import eu.jrie.put.piper.piperhomeservice.api.message.handleErrors
import eu.jrie.put.piper.piperhomeservice.domain.house.HousesService
import eu.jrie.put.piper.piperhomeservice.domain.user.asUser
import kotlinx.coroutines.reactor.asFlux
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@CrossOrigin
@RestController
@RequestMapping("houses")
class HousesController (
        private val service: HousesService
) {
    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun getHouse(
            auth: Authentication
    ): Mono<ResponseEntity<HouseResponse>> {
        return service.houseOfUser(auth.asUser())
                .map { HouseResponse(it.name, it.consents) }
                .map { ok(it) }
    }

    @GetMapping("rooms", produces = [APPLICATION_JSON_VALUE])
    fun getRooms(
            auth: Authentication
    ): Mono<ResponseEntity<ApiResponse>> {
       return service.roomsOfUsersHouse(auth.asUser())
               .asFlux()
               .map { it.asMessage() }
               .collectList()
               .map { RoomsResponse(it) }
               .map { ok(it as ApiResponse) }
               .handleErrors()
    }

    @GetMapping("rooms/{id}", produces = [APPLICATION_JSON_VALUE])
    fun getRoom(
            @PathVariable id: String,
            auth: Authentication
    ): Mono<ResponseEntity<ApiResponse>> {
        return service.roomDetails(id, auth.asUser())
                .map { (room, devices) -> RoomResponse(room.id, room.name, devices.asMessage()) }
                .map { ok(it as ApiResponse) }
                .handleErrors()
    }

}
