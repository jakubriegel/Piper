package eu.jrie.put.piper.piperhomeservice.api

import eu.jrie.put.piper.piperhomeservice.api.message.ApiResponse
import eu.jrie.put.piper.piperhomeservice.api.message.handleErrors
import eu.jrie.put.piper.piperhomeservice.domain.model.ModelService
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("models")
class ModelsController (
        private val service: ModelService
) {
    @PostMapping("{id}/ready")
    fun postReady(
            @PathVariable id: String
    ): Mono<ResponseEntity<ApiResponse>> {
        return service.setModelReady(id)
                .map { ok().build<ApiResponse>() }
                .handleErrors()
    }
}
