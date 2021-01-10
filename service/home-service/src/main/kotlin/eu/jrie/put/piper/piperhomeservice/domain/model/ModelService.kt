        package eu.jrie.put.piper.piperhomeservice.domain.model

import eu.jrie.put.piper.piperhomeservice.domain.suggestions.SuggestedRoutinesCreator
import eu.jrie.put.piper.piperhomeservice.domain.user.User
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import reactor.core.publisher.Mono.error
import reactor.core.publisher.Mono.zip
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import java.time.Instant.now

        @Service
@FlowPreview
class ModelService (
        private val modelRepository: ModelRepository,
        private val notReadyModelsRepository: NotReadyModelsRepository,
        private val suggestedRoutinesCreator: SuggestedRoutinesCreator
) {

    suspend fun addNewModel(model: NotReadyModel) {
        notReadyModelsRepository.insert(model).awaitSingle()
    }

    fun getLatestModel(user: User) = getLatestModel(user.house)

    fun getLatestModel(houseId: String): Mono<Model> {
        return modelRepository.findTopByHouseIdOrderByCreatedAt(houseId)
    }

    fun getNotReadyModel(houseId: String): Mono<NotReadyModel> {
        return notReadyModelsRepository.findAllByHouseId(houseId)
                .collectList()
                .flatMap {
                    when {
                        it.isEmpty() -> empty()
                        it.size == 1 -> it.first().toMono()
                        else -> error(IllegalStateException("There should be only one not ready model for house $houseId"))
                    }
                }
    }

    fun setModelReady(modelId: String): Mono<Void> {
        return notReadyModelsRepository.findById(modelId)
            .also { logger.info("Enabling model: $modelId") }
            .switchIfEmpty { throw ModelNotFoundException(modelId) }
            .map { Model(modelId, it.stagedAt, now(), it.houseId) }
            .flatMap { modelRepository.insert(it) }
            .flatMap {
                zip(
                    suggestedRoutinesCreator.createSuggestedRoutines(it.houseId),
                    notReadyModelsRepository.deleteById(it.id)
                )
            }
            .then()
    }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(ModelService::class.java)
    }
}
