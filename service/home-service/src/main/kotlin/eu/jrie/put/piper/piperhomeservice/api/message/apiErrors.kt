package eu.jrie.put.piper.piperhomeservice.api.message

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import eu.jrie.put.piper.piperhomeservice.api.RoutinesController
import eu.jrie.put.piper.piperhomeservice.domain.house.NotDeviceEventException
import eu.jrie.put.piper.piperhomeservice.domain.routine.PredictionsNotAvailableException
import eu.jrie.put.piper.piperhomeservice.domain.user.InsufficientAccessException
import eu.jrie.put.piper.piperhomeservice.infra.client.ServiceNotAvailableException
import eu.jrie.put.piper.piperhomeservice.infra.exception.PiperException
import eu.jrie.put.piper.piperhomeservice.infra.exception.PiperNotFoundException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactor.asFlux
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE
import org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.web.server.ServerWebInputException
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

data class ErrorResponse (
        val error: String,
        val message: String,
        val details: Map<String, Any?>
) : ApiResponse

fun Flow<ResponseEntity<ApiResponse>>.handleErrors() = asFlux().toMono().handleErrors()

fun Mono<ResponseEntity<ApiResponse>>.handleErrors() = onErrorResume { e ->
    logger.error("An error occurred", e)
    when(e) {
        is PiperException -> e.businessError()
        is ServerWebInputException -> e.badRequest()
        else -> e.internalServerError()
    } .let { Mono.just(it) }
}

private fun PiperException.businessError() = when (this) {
    is InsufficientAccessException -> FORBIDDEN
    is PredictionsNotAvailableException -> NO_CONTENT
    is PiperNotFoundException -> NOT_FOUND
    is NotDeviceEventException -> BAD_REQUEST
    is ServiceNotAvailableException -> SERVICE_UNAVAILABLE
    is RoutinesController.InvalidRoutineEventException -> BAD_REQUEST
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

private val logger: Logger = LoggerFactory.getLogger("ErrorLogger")
