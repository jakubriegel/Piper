package eu.jrie.put.piper.piperhomeservice.api.message

import eu.jrie.put.piper.piperhomeservice.api.HousesController
import eu.jrie.put.piper.piperhomeservice.api.RoutinesController
import eu.jrie.put.piper.piperhomeservice.api.message.util.Auth
import eu.jrie.put.piper.piperhomeservice.domain.routine.Routine
import eu.jrie.put.piper.piperhomeservice.domain.routine.RoutineConfiguration
import eu.jrie.put.piper.piperhomeservice.domain.routine.RoutineEvent
import eu.jrie.put.piper.piperhomeservice.domain.routine.RoutinePreview
import org.springframework.hateoas.IanaLinkRelations.COLLECTION
import org.springframework.hateoas.IanaLinkRelations.DESCRIBES
import org.springframework.hateoas.IanaLinkRelations.EDIT
import org.springframework.hateoas.IanaLinkRelations.FIRST
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn

data class RoutinesResponse (
        val routines: List<RoutinePreview>
) : RepresentationalResponse(
        linkTo(methodOn(RoutinesController::class.java).getRoutines(Auth)).withSelfRel(),
        linkTo(RoutinesController::class.java).slash(routines.first().id).withRel(FIRST),
        linkTo(HousesController::class.java).withRel(DESCRIBES)
)

data class RoutineResponse (
        val routine: RoutineMessage
) : RepresentationalResponse(
        linkTo(RoutinesController::class.java).slash(routine.id).withSelfRel(),
        linkTo(RoutinesController::class.java).slash(routine.id).withRel(EDIT),
        linkTo(methodOn(RoutinesController::class.java).getRoutines(Auth)).withRel(COLLECTION),
        linkTo(HousesController::class.java).withRel(DESCRIBES)
)

data class RoutineMessage (
        val id: String,
        val name: String,
        val enabled: Boolean,
        val events: List<RoutineEvent>,
        val configuration: RoutineConfiguration
)

fun Routine.asMessage() = RoutineMessage(id, name, enabled, events, configuration ?: RoutineConfiguration())

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
        val suggestions: List<RoutineEvent>,
        val n: Int
) : RepresentationalResponse(
        linkTo(RoutinesController::class.java).slash("suggestions?trigger=${start.trigger}&trigger=${start.action}&limit=$n").withSelfRel(),
        linkTo(methodOn(RoutinesController::class.java).getRoutines(Auth)).withRel(COLLECTION)
)