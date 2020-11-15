        package eu.jrie.put.piper.piperhomeservice.domain.model

import eu.jrie.put.piper.piperhomeservice.domain.user.User
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.Instant.now

@Service
class ModelService (
        private val modelRepository: ModelRepository,
        private val notReadyModelsRepository: NotReadyModelsRepository
) {

    suspend fun addNewModel(model: NotReadyModel) {
        notReadyModelsRepository.insert(model).awaitSingle()
    }

    fun getLatestModel(user: User) = getLatestModel(user.house)

    fun getLatestModel(houseId: String): Mono<Model> {
        return modelRepository.findTopByHouseIdOrderByCreatedAt(houseId)
    }

    fun setModelReady(modelId: String): Mono<Void> {
        return notReadyModelsRepository.findById(modelId)
                .switchIfEmpty { throw ModelNotFoundException(modelId) }
                .map { Model(modelId, it.stagedAt, now(), it.houseId) }
                .map { modelRepository.insert(it) }
                .then(empty())
    }
}
