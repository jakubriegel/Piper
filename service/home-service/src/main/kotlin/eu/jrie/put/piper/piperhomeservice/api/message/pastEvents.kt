package eu.jrie.put.piper.piperhomeservice.api.message

import eu.jrie.put.piper.piperhomeservice.domain.event.past.PastEvent
import java.time.Instant

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
