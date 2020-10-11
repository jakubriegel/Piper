package eu.jrie.put.piper.piperhomeservice.api

import eu.jrie.put.piper.piperhomeservice.api.PiperMediaType.TEXT_CSV_VALUE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PastEventsController {

    @PostMapping("/house/{id}/events", consumes = [TEXT_CSV_VALUE])
    suspend fun postEvents(
            @PathVariable id: String,
            @RequestBody events: Flow<EventMessage>
    ): ResponseEntity<Unit> {
        events.collect { logger.info(it.toString()) }
        return ok().build()
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
