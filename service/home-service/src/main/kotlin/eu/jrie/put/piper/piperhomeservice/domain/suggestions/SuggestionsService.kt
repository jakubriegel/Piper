package eu.jrie.put.piper.piperhomeservice.domain.suggestions

import eu.jrie.put.piper.piperhomeservice.domain.house.HousesService
import eu.jrie.put.piper.piperhomeservice.domain.model.Model
import eu.jrie.put.piper.piperhomeservice.domain.model.ModelService
import eu.jrie.put.piper.piperhomeservice.domain.routine.PredictionsNotAvailableException
import eu.jrie.put.piper.piperhomeservice.domain.routine.RoutineEvent
import eu.jrie.put.piper.piperhomeservice.domain.user.User
import eu.jrie.put.piper.piperhomeservice.infra.client.IntelligenceCoreServiceClient
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.Instant
import java.time.Instant.now

@Service
class SuggestionsService (
        private val suggestedRoutinesRepository: SuggestedRoutinesRepository,
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

    fun getSuggestedRoutines(n: Int, user: User): Flux<List<RoutineEvent>> {
        return suggestedRoutinesRepository.findRandom(n, user.house)
                .map { it.events }
    }

    private companion object {
        const val ML_EVENT_DELIMITER = '_'

        fun RoutineEvent.asMlEvent() = "$deviceId$ML_EVENT_DELIMITER$eventId"

        fun parseEvent(mlEvent: String) = mlEvent.split(ML_EVENT_DELIMITER)
                .let { (trigger, action) -> RoutineEvent(trigger, action) }
    }
}
