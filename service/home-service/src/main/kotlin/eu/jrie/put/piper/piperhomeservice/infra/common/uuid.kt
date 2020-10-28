package eu.jrie.put.piper.piperhomeservice.infra.common

import java.util.UUID.fromString
import java.util.UUID.randomUUID

val nextUUID: String
    get() = randomUUID().toString()

fun String.isUUID() = runCatching { fromString(this) }.isSuccess
