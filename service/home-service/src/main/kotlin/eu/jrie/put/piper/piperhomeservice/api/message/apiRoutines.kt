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
import org.springframework.hateoas.IanaLinkRelations.FIRST
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo

data class RoutinesResponse (
        val houseId: String,
        val routines: List<RoutinePreview>
) : RepresentationModel<RoutinesResponse>(), ApiResponse {
    init {
        add(linkTo(WebMvcLinkBuilder.methodOn(RoutinesController::class.java).getRoutines(Auth)).withSelfRel())
        add(linkTo(RoutinesController::class.java).slash(routines.first().id).withRel(FIRST))
        add(linkTo(HousesController::class.java).slash(houseId).withRel(DESCRIBES))
    }
}

data class RoutineResponse (
        val routine: RoutineMessage
) : RepresentationModel<RoutineResponse>(), ApiResponse {
    init {
        add(linkTo(RoutinesController::class.java).slash(routine.id).withSelfRel())
        add(linkTo(WebMvcLinkBuilder.methodOn(RoutinesController::class.java).getRoutines(Auth)).withRel(COLLECTION))
        add(linkTo(HousesController::class.java).withRel(DESCRIBES))
    }
}

data class RoutineMessage (
        val id: String,
        val name: String,
        val modelId: String?,
        val enabled: Boolean,
        val events: List<RoutineEvent>,
        val configuration: RoutineConfiguration
)

fun Routine.asMessage() = RoutineMessage(id!!, name, modelId, enabled, events, configuration ?: RoutineConfiguration())

data class RoutineRequest (
        val name: String,
        val enabled: Boolean,
        val events: List<RoutineEvent>,
        val configuration: RoutineConfiguration?
) : ApiRequest {
    fun toRoutine(houseId: String) = Routine(
            name, houseId, null, enabled, events, configuration ?: RoutineConfiguration()
    )
}
