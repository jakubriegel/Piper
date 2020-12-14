package eu.jrie.put.piper.piperhomeservice.api

import eu.jrie.put.piper.piperhomeservice.api.infra.DevicesProvider
import eu.jrie.put.piper.piperhomeservice.api.message.ApiResponse
import eu.jrie.put.piper.piperhomeservice.api.message.ContinuationResponse
import eu.jrie.put.piper.piperhomeservice.api.message.SuggestedRoutinesResponse
import eu.jrie.put.piper.piperhomeservice.api.message.asMessage
import eu.jrie.put.piper.piperhomeservice.api.message.handleErrors
import eu.jrie.put.piper.piperhomeservice.domain.routine.RoutineEvent
import eu.jrie.put.piper.piperhomeservice.domain.suggestions.SuggestionsService
import eu.jrie.put.piper.piperhomeservice.domain.user.asUser
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.reactor.asFlux
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import eu.jrie.put.piper.piperhomeservice.infra.common.component1
import eu.jrie.put.piper.piperhomeservice.infra.common.component2
import org.springframework.http.ResponseEntity.ok

@RestController
@RequestMapping("suggestions")
class SuggestionsController (
        private val service: SuggestionsService,
        private val devicesProvider: DevicesProvider
) {
    @GetMapping("continuation", produces = [APPLICATION_JSON_VALUE])
    @FlowPreview
    fun getContinuation(
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
                .zipWith(devicesProvider.getDevices(auth.asUser()))
                .map { (suggestions, devicesRooms) -> ContinuationResponse(start, suggestions.asMessage(devicesRooms), limit, params) }
                .map { ok(it as ApiResponse) }
                .handleErrors()
    }

    @GetMapping("routines", produces = [APPLICATION_JSON_VALUE])
    @FlowPreview
    fun getRoutines(
            @RequestParam(required = false, defaultValue = "5") limit: Int = 5,
            auth: Authentication
    ): Mono<ResponseEntity<ApiResponse>> {
        val params = mapOf("limit" to limit.toString())
        return service.getSuggestedRoutines(limit, auth.asUser())
                .collectList()
                .zipWith(devicesProvider.getDevices(auth.asUser()))
                .map { (suggestions, devicesRooms) ->
                    val msg = suggestions.map { it.asMessage(devicesRooms) }
                    SuggestedRoutinesResponse(msg, msg.size, params)
                }
                .map { ok(it as ApiResponse) }
                .handleErrors()
    }
}
