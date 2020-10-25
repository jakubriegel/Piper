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

fun Map<String, String>.asQuery() = when {
    isEmpty() -> ""
    else -> entries.joinToString(separator = "&", prefix = "?") { (k, v) -> "$k=$v" }
}

@Suppress("UNCHECKED_CAST")
fun params(vararg params: Pair<String, String?>): Map<String, String> {
    return params.filter { (_, v) -> v != null }
            .map { it as Pair<String, String> }
            .toTypedArray()
            .let { mapOf(*it) }
}
