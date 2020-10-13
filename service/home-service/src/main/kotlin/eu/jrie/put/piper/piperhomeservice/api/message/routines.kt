package eu.jrie.put.piper.piperhomeservice.api.message

import eu.jrie.put.piper.piperhomeservice.api.HousesController
import eu.jrie.put.piper.piperhomeservice.api.RoutinesController
import eu.jrie.put.piper.piperhomeservice.api.message.util.Auth
import eu.jrie.put.piper.piperhomeservice.domain.routine.Routine
import org.springframework.hateoas.IanaLinkRelations.DESCRIBES
import org.springframework.hateoas.IanaLinkRelations.FIRST
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo

data class RoutinesResponse (
        val houseId: String,
        val routines: List<Routine>
) : RepresentationModel<RoutinesResponse>() {
    init {
        add(linkTo(WebMvcLinkBuilder.methodOn(RoutinesController::class.java).getRoutines(Auth)).withSelfRel())
        add(linkTo(RoutinesController::class.java).slash(routines.first().id).withRel(FIRST))
        add(linkTo(HousesController::class.java).slash(houseId).withRel(DESCRIBES))
    }
}
