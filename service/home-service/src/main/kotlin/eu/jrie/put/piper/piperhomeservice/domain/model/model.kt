package eu.jrie.put.piper.piperhomeservice.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import java.time.Instant

@Document
data class NotReadyModel (
        @Id
        val id: String,
        val stagedAt: Instant
)

interface NotReadyModelsRepository : ReactiveMongoRepository<NotReadyModel, String>

@Document
data class Model (
        @Id
        val id: String,
        val stagedAt: Instant,
        val createdAt: Instant
)

interface ModelRepository : ReactiveMongoRepository<Model, String>
