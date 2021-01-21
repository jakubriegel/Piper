package eu.jrie.put.piper.piperhomeservice.domain.model

import eu.jrie.put.piper.piperhomeservice.HOUSE_ID
import eu.jrie.put.piper.piperhomeservice.MODEL_ID
import eu.jrie.put.piper.piperhomeservice.USER
import eu.jrie.put.piper.piperhomeservice.domain.suggestions.SuggestedRoutinesCreator
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono.empty
import reactor.core.publisher.Mono.just
import reactor.kotlin.core.publisher.toFlux
import java.time.Instant.now

@FlowPreview
internal class ModelServiceTest {

    private val modelRepository: ModelRepository = mockk()
    private val notReadyModelRepository: NotReadyModelsRepository = mockk()
    private val suggestedRoutinesCreator: SuggestedRoutinesCreator = mockk()

    private val service = ModelService(modelRepository, notReadyModelRepository, suggestedRoutinesCreator)

    @Test
    fun `should create new not ready model`() = runBlocking {
        // given
        val model = NotReadyModel(MODEL_ID, now(), HOUSE_ID, "path")
        every { notReadyModelRepository.insert(model) } returns just(model)

        // when
        service.addNewModel(model)

        // then
        verify { notReadyModelRepository.insert(model) }
        verify { modelRepository wasNot called }
    }

    @Test
    fun `should latest model by user`() = runBlocking {
        // given
        val model = Model(MODEL_ID, now(), now(), HOUSE_ID)
        every { modelRepository.findTopByHouseIdOrderByCreatedAt(HOUSE_ID) } returns just(model)

        // when
        val result = service.getLatestModel(USER).awaitSingle()

        // then
        verify { modelRepository.findTopByHouseIdOrderByCreatedAt(HOUSE_ID) }
        verify { notReadyModelRepository wasNot called }

        assertEquals(model, result)
    }

    @Test
    fun `should latest model by house id`() = runBlocking {
        // given
        val model = Model(MODEL_ID, now(), now(), HOUSE_ID)
        every { modelRepository.findTopByHouseIdOrderByCreatedAt(HOUSE_ID) } returns just(model)

        // when
        val result = service.getLatestModel(HOUSE_ID).awaitSingle()

        // then
        verify { modelRepository.findTopByHouseIdOrderByCreatedAt(HOUSE_ID) }
        verify { notReadyModelRepository wasNot called }

        assertEquals(model, result)
    }

    @Test
    fun `should activate model`() = runBlocking {
        // given
        val notReadyModel = NotReadyModel(MODEL_ID, now(), HOUSE_ID, "path")
        val model = Model(notReadyModel.id, notReadyModel.stagedAt, now(), notReadyModel.houseId)

        every { notReadyModelRepository.findById(MODEL_ID) } returns just(notReadyModel)
        every { modelRepository.insert(ofType(Model::class)) } returns just(model)
        every { suggestedRoutinesCreator.createSuggestedRoutines(HOUSE_ID) } returns empty()
        every { notReadyModelRepository.deleteById(MODEL_ID) } returns empty()

        // when
        service.setModelReady(MODEL_ID).awaitFirstOrNull()

        // then
        verify {
            notReadyModelRepository.findById(MODEL_ID)
            modelRepository.insert(match<Model> {
                it.id == notReadyModel.id &&
                it.stagedAt == notReadyModel.stagedAt &&
                it.houseId == notReadyModel.houseId &&
                it.createdAt.isAfter(notReadyModel.stagedAt)
            })
            suggestedRoutinesCreator.createSuggestedRoutines(HOUSE_ID)
            notReadyModelRepository.deleteById(MODEL_ID)
        }
    }

    @Test
    fun `should get not ready model for house`() = runBlocking {
        // given
        val model = NotReadyModel(MODEL_ID, now(), HOUSE_ID, "path")
        every { notReadyModelRepository.findAllByHouseId(HOUSE_ID) } returns listOf(model).toFlux()

        // when
        val result = service.getNotReadyModel(HOUSE_ID).awaitSingle()

        // then
        verify { notReadyModelRepository.findAllByHouseId(HOUSE_ID) }
        verify { modelRepository wasNot called }

        assertEquals(model, result)
    }

    @Test
    fun `should throw exception when more than one not ready model is present for house`() = runBlocking {
        // given
        every { notReadyModelRepository.findAllByHouseId(HOUSE_ID) } returns listOf<NotReadyModel>(mockk(), mockk()).toFlux()

        // when
        val result = runCatching { service.getNotReadyModel(HOUSE_ID).awaitSingle() }

        // then
        verify { notReadyModelRepository.findAllByHouseId(HOUSE_ID) }
        verify { modelRepository wasNot called }

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()!! is IllegalStateException)
    }
}
