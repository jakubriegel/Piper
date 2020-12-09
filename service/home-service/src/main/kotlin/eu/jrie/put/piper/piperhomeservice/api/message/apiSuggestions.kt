package eu.jrie.put.piper.piperhomeservice.api.message

import eu.jrie.put.piper.piperhomeservice.api.HousesController
import eu.jrie.put.piper.piperhomeservice.api.SuggestionsController
import eu.jrie.put.piper.piperhomeservice.domain.routine.RoutineEvent
import org.springframework.hateoas.IanaLinkRelations.ABOUT
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn

private val linkToSuggestions = linkTo(methodOn(SuggestionsController::class.java))
private val linkToHouses = linkTo(HousesController::class.java)

data class SuggestionsResponse (
        val start: RoutineEvent,
        val suggestions: List<RoutineEventMessage>,
        val n: Int,
        val params: Map<String, String?>
) : RepresentationalResponse(
        linkToSuggestions.slash("continuation").withQuery(params).withSelfRel(),
        linkToHouses.withRel(ABOUT)
)
