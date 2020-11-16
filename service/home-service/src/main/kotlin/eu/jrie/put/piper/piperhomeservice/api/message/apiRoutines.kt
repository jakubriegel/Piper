package eu.jrie.put.piper.piperhomeservice.api.message

import eu.jrie.put.piper.piperhomeservice.api.HousesController
import eu.jrie.put.piper.piperhomeservice.api.RoutinesController
import eu.jrie.put.piper.piperhomeservice.api.message.util.Auth
import eu.jrie.put.piper.piperhomeservice.domain.routine.Routine
import eu.jrie.put.piper.piperhomeservice.domain.routine.RoutineConfiguration
import eu.jrie.put.piper.piperhomeservice.domain.routine.RoutineEvent
import eu.jrie.put.piper.piperhomeservice.domain.routine.RoutinePreview
import org.springframework.hateoas.IanaLinkRelations.ABOUT
import org.springframework.hateoas.IanaLinkRelations.CANONICAL
import org.springframework.hateoas.IanaLinkRelations.COLLECTION
import org.springframework.hateoas.IanaLinkRelations.EDIT
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn

private val linkToRoutines = linkTo(methodOn(RoutinesController::class.java).getRoutines(Auth))
private val linkToHouses = linkTo(HousesController::class.java)

data class RoutinesResponse (
        val routines: List<RoutinePreviewMessage>
) : RepresentationalResponse(
        linkToRoutines.withSelfRel(),
        linkToHouses.withRel(ABOUT)
)

data class RoutinePreviewMessage (
        val id: String,
        val name: String,
        val enabled: Boolean
) : RepresentationalResponse(
        linkToRoutines.slash(id).withRel(CANONICAL)
)

fun List<RoutinePreview>.asMessage() = map {
    RoutinePreviewMessage(it.id, it.name, it.enabled)
}

data class RoutineResponse (
        val routine: RoutineMessage
) : RepresentationalResponse(
        linkToRoutines.slash(routine.id).withSelfRel(),
        linkToRoutines.slash(routine.id).withRel(EDIT),
        linkToRoutines.withRel(COLLECTION),
        linkToHouses.withRel(ABOUT)
)

data class RoutineMessage (
        val id: String,
        val name: String,
        val enabled: Boolean,
        val events: List<RoutineEventMessage>,
        val configuration: RoutineConfiguration
)

data class RoutineEventMessage (
        val deviceId: String,
        val eventId: String,
        val roomId: String
) : ApiMessage

fun Routine.asMessage(devicesRooms: Map<String, String>)
        = RoutineMessage(id, name, enabled, events.asMessage(devicesRooms), configuration ?: RoutineConfiguration())

fun List<RoutineEvent>.asMessage(devicesRooms: Map<String, String>) = map {
    RoutineEventMessage(it.deviceId, it.eventId, devicesRooms.getValue(it.deviceId))
}

data class RoutineRequest (
        val name: String,
        val enabled: Boolean,
        val events: List<RoutineEvent>,
        val configuration: RoutineConfiguration?
) : ApiRequest {
    fun toRoutine(houseId: String) = Routine(
            name, houseId, enabled, events, configuration ?: RoutineConfiguration()
    )
    fun toRoutine(id: String, houseId: String) = Routine(
            id, name, houseId, enabled, events, configuration ?: RoutineConfiguration()
    )
}

data class RoutineSuggestionsResponse (
        val start: RoutineEvent,
        val suggestions: List<RoutineEventMessage>,
        val n: Int,
        val params: Map<String, String?>
) : RepresentationalResponse(
        linkToRoutines.slash("suggestions").withQuery(params).withSelfRel(),
        linkToRoutines.withRel(COLLECTION)
)
