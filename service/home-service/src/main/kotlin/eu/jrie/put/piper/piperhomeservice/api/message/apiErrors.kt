package eu.jrie.put.piper.piperhomeservice.api.message

import eu.jrie.put.piper.piperhomeservice.domain.user.InsufficientAccessException
import eu.jrie.put.piper.piperhomeservice.infra.exception.PiperException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import reactor.core.publisher.Mono

data class ErrorResponse (
        val error: String,
        val message: String,
        val details: Map<String, Any?>
) : ApiResponse

fun Mono<ResponseEntity<ApiResponse>>.handleErrors() = onErrorResume {
    when(it) {
        is PiperException -> it.businessError()
        else -> it.internalServerError()
    } .let { r -> Mono.just(r) }
}

private fun PiperException.businessError() = when (this) {
    is InsufficientAccessException -> HttpStatus.FORBIDDEN
    else -> throw IllegalStateException("Unknown exception: $this")
}.let { status(it).body(this.asErrorResponse()) }

private fun PiperException.asErrorResponse(): ApiResponse = ErrorResponse(name, message.orEmpty(), details)

private fun Throwable.internalServerError(): ResponseEntity<ApiResponse> = ErrorResponse(
        "SERVER_ERROR", "An unknown error occurred",
        mapOf( "exception" to this::class.simpleName, "errorMessage" to message)
).let { status(INTERNAL_SERVER_ERROR).body(it) }
