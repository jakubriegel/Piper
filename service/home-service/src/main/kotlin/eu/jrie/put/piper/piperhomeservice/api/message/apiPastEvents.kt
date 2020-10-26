package eu.jrie.put.piper.piperhomeservice.api.message

import eu.jrie.put.piper.piperhomeservice.domain.event.past.PastEvent
import eu.jrie.put.piper.piperhomeservice.infra.common.isUUID
import java.time.Instant

interface PastEventsResponse : ApiResponse

data class PastEventsErrorsResponse (
        val invalidEvents: List<InvalidEventMessage>
) : PastEventsResponse

data class InvalidEventMessage (
        val line: Int,
        val event: EventMessage
)

data class EventMessage (
        val deviceId: String?,
        val eventId: String?,
        val time: String?
) : ApiResponse {
    fun validData(): Boolean {
        val validTrigger = !deviceId.isNullOrBlank() && deviceId.isUUID()
        val validAction = !eventId.isNullOrBlank() && eventId.isUUID()
        val validTime = time != null && runCatching { time!!.toInt() }.getOrNull() ?: -1 > 0
        return validTrigger and validAction and validTime
    }
    fun asPastEvent(houseId: String) = PastEvent(
            houseId, deviceId!!, eventId!!, Instant.ofEpochSecond(time?.toLong()!!)
    )
}
