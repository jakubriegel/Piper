package eu.jrie.put.piper.piperhomeservice.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono
import java.time.Instant

@Document
data class NotReadyModel (
        @Id
        val id: String,
        val stagedAt: Instant,
        val houseId: String,
        val dataFilePath: String
)

interface NotReadyModelsRepository : ReactiveMongoRepository<NotReadyModel, String>

@Document
data class Model (
        @Id
        val id: String,
        val stagedAt: Instant,
        val createdAt: Instant,
        val houseId: String
)

interface ModelRepository : ReactiveMongoRepository<Model, String> {
        fun findTopByHouseIdOrderByCreatedAt(houseId: String): Mono<Model>
}

data class NewModelEvent (
        val modelId: String,
        val path: String
)
