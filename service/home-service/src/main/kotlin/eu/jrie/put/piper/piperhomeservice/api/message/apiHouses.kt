package eu.jrie.put.piper.piperhomeservice.api.message

import eu.jrie.put.piper.piperhomeservice.api.HousesController
import eu.jrie.put.piper.piperhomeservice.api.RoutinesController
import eu.jrie.put.piper.piperhomeservice.domain.house.Consents
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder

data class HouseResponse (
        val name: String,
        val consents: Consents
): RepresentationModel<HouseResponse>(), ApiResponse {
    init {
        add(WebMvcLinkBuilder.linkTo(HousesController::class.java).withSelfRel())
        add(WebMvcLinkBuilder.linkTo(RoutinesController::class.java).withRel(IanaLinkRelations.DESCRIBED_BY))
    }
}
