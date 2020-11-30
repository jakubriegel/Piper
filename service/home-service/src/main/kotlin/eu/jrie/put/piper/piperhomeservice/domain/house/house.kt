package eu.jrie.put.piper.piperhomeservice.domain.house

import eu.jrie.put.piper.piperhomeservice.domain.routine.RoutinePreview
import eu.jrie.put.piper.piperhomeservice.infra.repository.CustomRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.query.BasicQuery
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Document
data class House (
        @Id
        val id: String,
        val name: String,
        val consents: Consents
)

data class Consents (
        val behaviourBasedLearning: Boolean = true
)

interface HousesRepository : ReactiveMongoRepository<House, String>, HousesRepositoryCustom

interface HousesRepositoryCustom {
        fun findWithLearningConsent(): Flow<String>
}

@Repository
class HousesRepositoryCustomImpl (
        template: ReactiveMongoTemplate
) : HousesRepositoryCustom, CustomRepository(template) {
        override fun findWithLearningConsent(): Flow<String> {
                val projection = projectionOf("id" to 1)
                val query = BasicQuery(where("consents.behaviourBasedLearning").`is`(true).criteriaObject, projection)
                return template.find(query, JustId::class.java, "house")
                        .asFlow()
                        .map { it.id }
        }
}

data class JustId (
        val id: String
)
