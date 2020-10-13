package eu.jrie.put.piper.piperhomeservice.api

import eu.jrie.put.piper.piperhomeservice.api.PiperMediaType.TEXT_CSV_VALUE
import eu.jrie.put.piper.piperhomeservice.domain.event.past.PastEvent
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
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.*

@RestController
class PastEventsController (
        private val service: PastEventService
) {

    @PostMapping("/house/events", consumes = [TEXT_CSV_VALUE])
    suspend fun postEvents(
            @RequestBody events: Flow<EventMessage>,
            auth: Authentication
    ): ResponseEntity<PastEventsResponse> {
        val houseId = auth.asUser().houses.first()
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

interface PastEventsResponse

data class PastEventsErrorsResponse (
        val invalidEvents: List<InvalidEventMessage>
) : PastEventsResponse

data class InvalidEventMessage (
        val line: Int,
        val event: EventMessage
)

data class EventMessage (
        val trigger: String?,
        val action: String?,
        val time: String?
) {
    fun validData(): Boolean {
        val validTrigger = !trigger.isNullOrBlank()
        val validAction = !action.isNullOrBlank()
        val validTime = time != null && runCatching { time!!.toInt() }.getOrNull() ?: -1 > 0
        return validTrigger and validAction and validTime
    }
    fun asPastEvent(houseId: String) = PastEvent(
            houseId, trigger!!, action!!, Instant.ofEpochSecond(time?.toLong()!!)
    )
}
