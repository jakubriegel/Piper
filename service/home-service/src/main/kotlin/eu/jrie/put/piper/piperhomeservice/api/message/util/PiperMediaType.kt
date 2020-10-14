package eu.jrie.put.piper.piperhomeservice.api.message.util

import org.springframework.http.MediaType.parseMediaType

object PiperMediaType {
    const val TEXT_CSV_VALUE = "text/csv"
    val TEXT_CSV = parseMediaType(TEXT_CSV_VALUE)
}