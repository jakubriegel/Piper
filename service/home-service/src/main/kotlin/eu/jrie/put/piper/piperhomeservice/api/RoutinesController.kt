package eu.jrie.put.piper.piperhomeservice.api

import eu.jrie.put.piper.piperhomeservice.api.message.ApiResponse
import eu.jrie.put.piper.piperhomeservice.api.message.RoutineRequest
import eu.jrie.put.piper.piperhomeservice.api.message.RoutineResponse
import eu.jrie.put.piper.piperhomeservice.api.message.RoutinesResponse
import eu.jrie.put.piper.piperhomeservice.api.message.asMessage
import eu.jrie.put.piper.piperhomeservice.api.message.handleErrors
import eu.jrie.put.piper.piperhomeservice.domain.routine.Routine
import eu.jrie.put.piper.piperhomeservice.domain.routine.RoutinesService
import eu.jrie.put.piper.piperhomeservice.domain.user.asUser
import kotlinx.coroutines.reactor.asFlux
import org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.created
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.http.ResponseEntity.status
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.net.URI

@RestController
@RequestMapping("routines")
class RoutinesController(
        private val service: RoutinesService
) {
    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun getRoutines(
            auth: Authentication
    ): Mono<RoutinesResponse> {
        val houseId = auth.asUser().house
        return service.routinesForHouse(houseId)
                .asFlux()
                .collectList()
                .map { RoutinesResponse(it) }
    }

    @GetMapping("{id}", produces = [APPLICATION_JSON_VALUE])
    fun getRoutine(
            @PathVariable id: String,
            auth: Authentication
    ): Mono<ResponseEntity<ApiResponse>> {
        return service.routineById(id, auth.asUser())
                .map { RoutineResponse(it.asMessage()) }
                .map { ok(it as ApiResponse) }
                .handleErrors()
                .defaultIfEmpty(notFound().build())
    }

    @PostMapping(consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
    fun postRoutine(
            @RequestBody routine: Mono<RoutineRequest>,
            auth: Authentication
    ): Mono<ResponseEntity<ApiResponse>> {
        val houseId = auth.asUser().house
        return routine.flatMap { service.createRoutine(it.toRoutine(houseId)) }
                .map {
                    created(URI.create("/routines/${it.id}"))
                        .body(RoutineResponse(it.asMessage()) as ApiResponse)
                }
                .handleErrors()
    }

    @PutMapping(consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
    fun putRoutine(
            @RequestBody routine: Mono<Routine>,
            auth: Authentication
    ): Mono<ResponseEntity<Unit>> = Mono.just(status(SERVICE_UNAVAILABLE).build())
}
