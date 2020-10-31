package eu.jrie.put.piper.piperhomeservice.api

import eu.jrie.put.piper.piperhomeservice.api.message.ApiResponse
import eu.jrie.put.piper.piperhomeservice.api.message.DeviceTypesResponse
import eu.jrie.put.piper.piperhomeservice.api.message.DevicesResponse
import eu.jrie.put.piper.piperhomeservice.api.message.HouseCreatedResponse
import eu.jrie.put.piper.piperhomeservice.api.message.HouseResponse
import eu.jrie.put.piper.piperhomeservice.api.message.HouseSchemaResponse
import eu.jrie.put.piper.piperhomeservice.api.message.RoomsResponse
import eu.jrie.put.piper.piperhomeservice.api.message.asMessage
import eu.jrie.put.piper.piperhomeservice.api.message.asResponse
import eu.jrie.put.piper.piperhomeservice.api.message.handleErrors
import eu.jrie.put.piper.piperhomeservice.api.message.params
import eu.jrie.put.piper.piperhomeservice.domain.house.HousesService
import eu.jrie.put.piper.piperhomeservice.domain.house.NewHouseSchema
import eu.jrie.put.piper.piperhomeservice.domain.user.asUser
import eu.jrie.put.piper.piperhomeservice.infra.common.component1
import eu.jrie.put.piper.piperhomeservice.infra.common.component2
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.asFlux
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.created
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.net.URI

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
        return service.getHouse(auth.asUser())
                .map { it.asResponse() }
                .map { ok(it) }
    }

    @PostMapping(produces = [APPLICATION_JSON_VALUE])
    fun postHouse(): Mono<ResponseEntity<ApiResponse>> {
        return service.createHouse()
                .map { (house, ownerUser, houseUser) ->
                    HouseCreatedResponse(house.asResponse(), ownerUser.login, houseUser.login)
                }
                .map { created(URI.create("houses")).body(it) }
    }

    @PutMapping("schema", consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
    suspend fun putSchema(
            @RequestBody schema: NewHouseSchema,
            auth: Authentication
    ): ResponseEntity<ApiResponse> {
        val user = auth.asUser()
        return service.updateSchema(schema, user)
                .then(Mono.zip(
                        service.getDevicesTypes(user)
                                .map { it.asResponse() }
                                .asFlux().collectList(),
                        service.getRooms(auth.asUser())
                                .asFlux()
                                .map { it to service.getDevices(it.id, user) }
                                .asFlow()
                                .map { it.asResponse() }
                                .asFlux().collectList()
                ))
                .map { (deviceTypes, rooms) -> HouseSchemaResponse(deviceTypes, rooms) }
                .map { ok(it as ApiResponse) }
                .handleErrors()
                .awaitFirst()
    }

    @GetMapping("rooms", produces = [APPLICATION_JSON_VALUE])
    fun getRooms(
            auth: Authentication
    ): Mono<ResponseEntity<ApiResponse>> {
       return service.getRooms(auth.asUser())
               .map { it.asMessage() }
               .asFlux()
               .collectList()
               .map { RoomsResponse(it) }
               .map { ok(it as ApiResponse) }
               .handleErrors()
    }

    @GetMapping("devices", produces = [APPLICATION_JSON_VALUE])
    @FlowPreview
    fun getDevices(
            @RequestParam(required = false) roomId: String?,
            auth: Authentication
    ): Mono<ResponseEntity<ApiResponse>> {
        return when (roomId) {
            null -> service.getDevices(auth.asUser())
            else -> service.getDevices(roomId, auth.asUser())
        }.map { it.asMessage() }
                .asFlux()
                .collectList()
                .map { DevicesResponse(it, params("roomId" to roomId)) }
                .map { ok(it as ApiResponse) }
                .handleErrors()
    }

    @GetMapping("devices/{id}", produces = [APPLICATION_JSON_VALUE])
    fun getDevice(
            @PathVariable id: String,
            auth: Authentication
    ): Mono<ResponseEntity<ApiResponse>> {
        return service.getDevice(id, auth.asUser())
                .map { it.asMessage() }
                .map { ok(it as ApiResponse) }
                .handleErrors()
                .defaultIfEmpty(notFound().build())
    }

    @GetMapping("devices/types", produces = [APPLICATION_JSON_VALUE])
    fun getDeviceTypes(
            auth: Authentication
    ): Mono<ResponseEntity<ApiResponse>> {
        return service.getDevicesTypes(auth.asUser())
                .map { it.asResponse() }
                .asFlux()
                .collectList()
                .map { DeviceTypesResponse(it) }
                .map { ok(it as ApiResponse) }
                .handleErrors()
    }

    @GetMapping("devices/types/{id}", produces = [APPLICATION_JSON_VALUE])
    fun getDeviceType(
            @PathVariable id: String,
            auth: Authentication
    ): Mono<ResponseEntity<ApiResponse>> {
        return service.getDeviceType(id, auth.asUser())
                .map { it.asResponse() }
                .map { ok(it as ApiResponse) }
                .handleErrors()
                .defaultIfEmpty(notFound().build())
    }

    @GetMapping("devices/events/{id}", produces = [APPLICATION_JSON_VALUE])
    fun getEvent(
            @PathVariable id: String,
            auth: Authentication
    ): Mono<ResponseEntity<ApiResponse>> {
        return service.getEvent(id, auth.asUser())
                .map { it.asResponse() }
                .map { ok(it as ApiResponse) }
                .handleErrors()
                .defaultIfEmpty(notFound().build())
    }

}
