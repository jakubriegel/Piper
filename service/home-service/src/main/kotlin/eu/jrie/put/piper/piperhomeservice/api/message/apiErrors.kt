package eu.jrie.put.piper.piperhomeservice.api.message

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import eu.jrie.put.piper.piperhomeservice.domain.user.InsufficientAccessException
import eu.jrie.put.piper.piperhomeservice.infra.exception.PiperException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.web.server.ServerWebInputException
import reactor.core.publisher.Mono

data class ErrorResponse (
        val error: String,
        val message: String,
        val details: Map<String, Any?>
) : ApiResponse

fun Mono<ResponseEntity<ApiResponse>>.handleErrors() = onErrorResume { e ->
    when(e) {
        is PiperException -> e.businessError()
        is ServerWebInputException -> e.badRequest()
        else -> e.internalServerError()
    } .let { Mono.just(it) }
}

private fun PiperException.businessError() = when (this) {
    is InsufficientAccessException -> HttpStatus.FORBIDDEN
    else -> throw IllegalStateException("Unknown exception: $this")
}.let { status(it).body(this.asErrorResponse()) }

private fun PiperException.asErrorResponse(): ApiResponse = ErrorResponse(name, message.orEmpty(), details)

private fun ServerWebInputException.badRequest(): ResponseEntity<ApiResponse> = when (val cause = mostSpecificCause) {
    is JsonMappingException -> {
        val details = mapOf(
                "path" to cause.path.joinToString(separator = ".") {
                    if (it.fieldName != null) it.fieldName
                    else "[${it.index}]"
                },
                "line" to cause.location.lineNr,
                "column" to cause.location.columnNr
        )
        val response = ErrorResponse("INVALID_MESSAGE", "You have error in your message.", details)
        status(UNPROCESSABLE_ENTITY).body(response)
    }
    is JsonParseException -> {
        val details = mapOf(
                "line" to cause.location.lineNr,
                "column" to cause.location.columnNr
        )
        val response = ErrorResponse("INVALID_JSON", cause.originalMessage, details)
        status(UNPROCESSABLE_ENTITY).body(response)
    }
    else -> {
        val response = ErrorResponse("BAD_REQUEST", message, emptyMap())
        status(status).body(response)
    }
}

private fun Throwable.internalServerError(): ResponseEntity<ApiResponse> = ErrorResponse(
        "SERVER_ERROR", "An unknown error occurred",
        mapOf( "exception" to this::class.simpleName, "errorMessage" to message)
).let { status(INTERNAL_SERVER_ERROR).body(it) }
