package eu.jrie.put.piper.piperhomeservice.api

import eu.jrie.put.piper.piperhomeservice.api.PiperMediaType.TEXT_CSV_VALUE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.reactive.awaitFirst
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.security.Principal
import java.time.Instant
import eu.jrie.put.piper.piperhomeservice.domain.event.past.PastEvent
import eu.jrie.put.piper.piperhomeservice.domain.event.past.PastEventService
import kotlinx.coroutines.flow.map
import java.util.*

@RestController
class PastEventsController (
        private val service: PastEventService
) {

    @PostMapping("/house/events", consumes = [TEXT_CSV_VALUE])
    suspend fun postEvents(
            @RequestBody events: Flow<EventMessage>,
            auth: Authentication
    ): ResponseEntity<Mono<Unit>> {
        val houseId = auth.name
        events.map {
            PastEvent(UUID.randomUUID(), it.trigger!!, it.action!!, Instant.ofEpochSecond(it.time!!))
        }.let { service.add(it) }
        return ok(Mono.empty())
    }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(PastEventsController::class.java)
    }
}

data class EventMessage (
        val trigger: String?,
        val action: String?,
        val time: Long?
)
