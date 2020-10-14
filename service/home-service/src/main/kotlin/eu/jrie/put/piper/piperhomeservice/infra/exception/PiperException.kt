package eu.jrie.put.piper.piperhomeservice.infra.exception

abstract class PiperException (
        message: String
) : Exception(message) {

    val name: String
        get() = this::class.simpleName!!
                .replace("Exception", "")
                .replace(Regex("([a-z])([A-Z])"), "$1_$2")
                .toUpperCase()

    open val details: Map<String, Any?>
        get() = emptyMap()
}
