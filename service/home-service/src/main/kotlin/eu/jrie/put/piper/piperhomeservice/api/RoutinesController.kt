package eu.jrie.put.piper.piperhomeservice.api

import eu.jrie.put.piper.piperhomeservice.api.message.RoutinesResponse
import eu.jrie.put.piper.piperhomeservice.domain.routine.Routine
import eu.jrie.put.piper.piperhomeservice.domain.routine.RoutinesService
import eu.jrie.put.piper.piperhomeservice.domain.user.asUser
import kotlinx.coroutines.reactor.asFlux
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("routines")
class RoutinesController (
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
                .map { RoutinesResponse(houseId, it) }
    }

    @GetMapping("{id}", produces = [APPLICATION_JSON_VALUE])
    fun getRoutine(
            @PathVariable id: String,
            auth: Authentication
    ) {

    }

    @PostMapping(consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
    fun postRoutine(
            @RequestBody routine: Mono<Routine>,
            auth: Authentication
    ) {

    }

    @PutMapping(consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
    fun putRoutine(
            @RequestBody routine: Mono<Routine>,
            auth: Authentication
    ) {

    }
}
