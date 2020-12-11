package eu.jrie.put.piper.piperhomeservice.api

import eu.jrie.put.piper.piperhomeservice.api.message.ApiResponse
import eu.jrie.put.piper.piperhomeservice.api.message.RoutineEventMessage
import eu.jrie.put.piper.piperhomeservice.api.message.RoutineRequest
import eu.jrie.put.piper.piperhomeservice.api.message.RoutineResponse
import eu.jrie.put.piper.piperhomeservice.api.message.RoutineSuggestionsResponse
import eu.jrie.put.piper.piperhomeservice.api.message.RoutinesResponse
import eu.jrie.put.piper.piperhomeservice.api.message.asMessage
import eu.jrie.put.piper.piperhomeservice.api.message.handleErrors
import eu.jrie.put.piper.piperhomeservice.domain.house.HousesService
import eu.jrie.put.piper.piperhomeservice.domain.routine.RoutineEvent
import eu.jrie.put.piper.piperhomeservice.domain.routine.RoutinesService
import eu.jrie.put.piper.piperhomeservice.domain.user.User
import eu.jrie.put.piper.piperhomeservice.domain.user.asUser
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.asFlux
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.created
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.security.core.Authentication
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
import eu.jrie.put.piper.piperhomeservice.infra.common.component1
import eu.jrie.put.piper.piperhomeservice.infra.common.component2
import eu.jrie.put.piper.piperhomeservice.infra.exception.PiperException
import org.springframework.http.ResponseEntity.noContent
import org.springframework.web.bind.annotation.DeleteMapping
import reactor.core.publisher.Mono.just

@RestController
@RequestMapping("routines")
class RoutinesController(
        private val service: RoutinesService,
        private val housesService: HousesService
) {
    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun getRoutines(
            @RequestParam(required = false) enabled: Boolean?,
            auth: Authentication
    ): Mono<RoutinesResponse> {
        val houseId = auth.asUser().house
        return service.routinesForHouse(houseId)
                .asFlux()
                .let { routines ->
                    if (enabled != null) routines.filter { it.enabled }
                    else routines
                }
                .collectList()
                .map { RoutinesResponse(it.asMessage()) }
    }

    @GetMapping("{id}", produces = [APPLICATION_JSON_VALUE])
    fun getRoutine(
            @PathVariable id: String,
            auth: Authentication
    ): Mono<ResponseEntity<ApiResponse>> {
        return service.routineById(id, auth.asUser())
                .zipWith(getDevicesFromUser(auth.asUser()))
                .map { (routine, devicesRooms) -> RoutineResponse(routine.asMessage(devicesRooms)) }
                .map { ok(it as ApiResponse) }
                .handleErrors()
                .defaultIfEmpty(notFound().build())
    }

    @PostMapping(consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
    fun postRoutine(
            @RequestBody routine: Mono<RoutineRequest>,
            auth: Authentication
    ): Mono<ResponseEntity<ApiResponse>> {
        val user = auth.asUser()
        return routine.checkDevicesRooms(user)
                .flatMap { service.createRoutine(it.toRoutine(user.house)) }
                .zipWith(getDevicesFromUser(user))
                .map { (routine, devicesRooms) -> RoutineResponse(routine.asMessage(devicesRooms)) }
                .map {
                    created(URI.create("/routines/${it.routine.id}"))
                        .body(it as ApiResponse)
                    }
                .handleErrors()
    }

    @PutMapping("{id}", consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
    fun putRoutine(
            @PathVariable id: String,
            @RequestBody routine: Mono<RoutineRequest>,
            auth: Authentication
    ): Mono<ResponseEntity<ApiResponse>> {
        val user = auth.asUser()
        return routine.checkDevicesRooms(user)
                .flatMap { service.updateRoutine(it.toRoutine(id, user.house), user) }
                .zipWith(getDevicesFromUser(auth.asUser()))
                .map { (routine, devicesRooms) -> RoutineResponse(routine.asMessage(devicesRooms)) }
                .map { ok(it as ApiResponse) }
                .handleErrors()
    }

    @DeleteMapping("{id}")
    fun deleteRoutine(
            @PathVariable id: String,
            auth: Authentication
    ): Mono<ResponseEntity<ApiResponse>> {
        return service.deleteRoutine(id, auth.asUser())
                .thenReturn(noContent().build<ApiResponse>())
                .handleErrors()
    }

    @GetMapping("suggestions", produces = [APPLICATION_JSON_VALUE])
    @FlowPreview
    fun getSuggestions(
            @RequestParam(required = true) deviceId: String,
            @RequestParam(required = true) eventId: String,
            @RequestParam(required = false, defaultValue = "5") limit: Int = 5,
            auth: Authentication
    ): Mono<ResponseEntity<ApiResponse>> {
        val params = mapOf("deviceId" to deviceId, "eventId" to eventId, "limit" to limit.toString())
        val start = RoutineEvent(deviceId, eventId)
        return service.getContinuationSuggestions(start, limit, auth.asUser())
                .asFlux()
                .collectList()
                .zipWith(getDevicesFromUser(auth.asUser()))
                .map { (suggestions, devicesRooms) -> RoutineSuggestionsResponse(start, suggestions.asMessage(devicesRooms), limit, params) }
                .map { ok(it as ApiResponse) }
                .handleErrors()
    }

    private fun Mono<RoutineRequest>.checkDevicesRooms(user: User) = zipWith(getDevicesFromUser(user))
            .map { (request, devicesRooms) ->
                request.events.forEach {
                    if (devicesRooms[it.deviceId] != it.roomId) throw InvalidRoutineEventException(it)
                }
                request
            }

    private fun getDevicesFromUser(user: User): Mono<Map<String, String>> {
        return housesService.getDevices(user)
                .map { it.id to it.roomId }
                .asFlux()
                .collectList()
                .map { it.toTypedArray() }
                .map { mapOf(*it) }
    }

    class InvalidRoutineEventException(
            event: RoutineEventMessage
    ) : PiperException("Provided roomId, deviceId and eventId do not match.") {
        override val details = mapOf("givenEvent" to event)
    }
}
