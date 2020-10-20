package eu.jrie.put.piper.piperhomeservice.api

import eu.jrie.put.piper.piperhomeservice.api.message.EventMessage
import eu.jrie.put.piper.piperhomeservice.api.message.InvalidEventMessage
import eu.jrie.put.piper.piperhomeservice.api.message.PastEventsErrorsResponse
import eu.jrie.put.piper.piperhomeservice.api.message.PastEventsResponse
import eu.jrie.put.piper.piperhomeservice.api.message.util.PiperMediaType.TEXT_CSV_VALUE
import eu.jrie.put.piper.piperhomeservice.domain.event.past.PastEventService
import eu.jrie.put.piper.piperhomeservice.domain.user.asUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.withIndex
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.http.ResponseEntity.unprocessableEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("house")
class PastEventsController (
        private val service: PastEventService
) {

    @PostMapping("events", consumes = [TEXT_CSV_VALUE])
    suspend fun postEvents(
            @RequestBody events: Flow<EventMessage>,
            auth: Authentication
    ): ResponseEntity<PastEventsResponse> {
        val houseId = auth.asUser().house
        val invalid = mutableListOf<InvalidEventMessage>()

        events.withIndex()
                .filter { (i, event) ->
                    event.validData().also { if (!it) invalid.add(InvalidEventMessage(i+1, event)) }
                }
                .map { (_, event) -> event.asPastEvent(houseId) }
                .let { service.add(it) }

        return if (invalid.isEmpty()) ok().build()
        else PastEventsErrorsResponse(invalid)
                .let { unprocessableEntity().body(it) }
    }
}
