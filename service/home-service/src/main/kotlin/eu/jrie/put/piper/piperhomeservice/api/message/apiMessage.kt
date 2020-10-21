package eu.jrie.put.piper.piperhomeservice.api.message

import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel

interface ApiMessage

interface ApiRequest : ApiMessage

interface ApiResponse : ApiMessage

abstract class RepresentationalResponse (
        vararg links: Link
) : RepresentationModel<RepresentationalResponse>(), ApiResponse {

    init {
        addLinks(*links)
    }

    private fun addLinks(vararg links: Link) = add(*links)
}
