package eu.jrie.put.piper.piperhomeservice.domain.suggestions

import eu.jrie.put.piper.piperhomeservice.domain.routine.RoutineEvent
import eu.jrie.put.piper.piperhomeservice.infra.repository.CustomRepository
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.sample
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Document
data class SuggestedRoutine(
        @Id
        val id: String,
        val houseId: String,
        val events: List<RoutineEvent>
)

interface SuggestedRoutinesRepository : ReactiveMongoRepository<SuggestedRoutine, String>, SuggestedRoutinesRepositoryCustom {
    fun deleteAllByHouseId(houseId: String): Mono<Void>
}

interface SuggestedRoutinesRepositoryCustom {
    fun findRandom(n: Int, houseId: String): Flux<SuggestedRoutine>
}

@Repository
class SuggestedRoutinesRepositoryCustomImpl(
        template: ReactiveMongoTemplate
) : SuggestedRoutinesRepositoryCustom, CustomRepository(template) {
    override fun findRandom(n: Int, houseId: String): Flux<SuggestedRoutine> {
        return newAggregation(
                match(where("houseId").`is`(houseId)),
                sample(n.toLong())
        ) .let {
            template.aggregate(it, "suggestedRoutine", SuggestedRoutine::class.java)
        }
    }
}
