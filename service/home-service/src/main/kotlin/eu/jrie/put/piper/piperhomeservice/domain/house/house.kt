package eu.jrie.put.piper.piperhomeservice.domain.house

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import java.time.Instant

@Document
data class House (
        @Id
        val id: String,
        val name: String,
        val models: Models,
        val consents: Consents
)

data class Models (
        val current: Model,
        val past: Set<Model>
)

data class Model (
        @Field("id")
        val id: String,
        val createdAt: Instant
)

data class Consents (
        val behaviourBasedLearning: Boolean
)

interface HousesRepository : ReactiveMongoRepository<House, String>
