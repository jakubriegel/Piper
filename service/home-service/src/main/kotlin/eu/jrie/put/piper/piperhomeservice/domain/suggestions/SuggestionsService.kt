package eu.jrie.put.piper.piperhomeservice.domain.suggestions

import eu.jrie.put.piper.piperhomeservice.domain.house.HousesService
import eu.jrie.put.piper.piperhomeservice.domain.model.ModelService
import eu.jrie.put.piper.piperhomeservice.domain.routine.PredictionsNotAvailableException
import eu.jrie.put.piper.piperhomeservice.domain.routine.RoutineEvent
import eu.jrie.put.piper.piperhomeservice.domain.user.User
import eu.jrie.put.piper.piperhomeservice.infra.client.IntelligenceCoreServiceClient
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import org.springframework.stereotype.Service
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class SuggestionsService (
        private val housesService: HousesService,
        private val modelService: ModelService,
        private val intelligenceClient: IntelligenceCoreServiceClient
) {
    @FlowPreview
    fun getContinuationSuggestions(start: RoutineEvent, n: Int, user: User) =
            housesService.checkIsEventOfDevice(start.deviceId, start.eventId, user)
                    .then(modelService.getLatestModel(user))
                    .switchIfEmpty { throw PredictionsNotAvailableException() }
                    .map { it.id }
                    .asFlow()
                    .flatMapConcat { getContinuationSuggestions(start, n, it) }

    @FlowPreview
    private fun getContinuationSuggestions(start: RoutineEvent, n: Int, modelId: String) =
            flowOf(start)
                    .map { it.asMlEvent() }
                    .flatMapConcat { intelligenceClient.getSequence(modelId, it, n) }
                    .map { parseEvent(it) }
                    .filter { it != start }

    private companion object {
        const val ML_EVENT_DELIMITER = '_'

        fun RoutineEvent.asMlEvent() = "$deviceId$ML_EVENT_DELIMITER$eventId"

        fun parseEvent(mlEvent: String) = mlEvent.split(ML_EVENT_DELIMITER)
                .let { (trigger, action) -> RoutineEvent(trigger, action) }
    }
}
