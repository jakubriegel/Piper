package eu.jrie.put.piper.piperhomeservice.api.message

import com.fasterxml.jackson.databind.ObjectReader
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.dataformat.csv.CsvSchema.builder
import eu.jrie.put.piper.piperhomeservice.api.EventMessage
import eu.jrie.put.piper.piperhomeservice.api.PiperMediaType.TEXT_CSV
import org.slf4j.LoggerFactory
import org.springframework.core.ResolvableType
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.MediaType
import org.springframework.http.ReactiveHttpInputMessage
import org.springframework.http.codec.HttpMessageReader
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.util.concurrent.atomic.AtomicReference


@Component
class EventMessageReader(
        mapper: CsvMapper
) : HttpMessageReader<EventMessage> {

    private val reader = mapper.readerFor(EventMessage::class.java).with(schema)

    override fun getReadableMediaTypes() = listOf(TEXT_CSV)

    override fun canRead(elementType: ResolvableType, mediaType: MediaType?): Boolean =
            elementType.rawClass == EventMessage::class.java && mediaType == TEXT_CSV

    override fun read(
            elementType: ResolvableType, message: ReactiveHttpInputMessage, hints: MutableMap<String, Any>
    ) = readEvents(message)

    override fun readMono(
            elementType: ResolvableType, message: ReactiveHttpInputMessage, hints: MutableMap<String, Any>
    ) = readEvents(message).single()

    private fun readEvents(message: ReactiveHttpInputMessage) = Mono.fromCallable {
        val lastRow = AtomicReference(emptyList<Byte>())
        message.body
                .map { it.asCsvChunk(lastRow) }
                .map { reader.readValues<EventMessage>(it) }
                .flatMap { it.toFlux() }
                .let { Flux.concat(it, lastRow.toLastChunk(reader)) }
    }.flatMapMany { it }

    private companion object {
        val logger: org.slf4j.Logger = LoggerFactory.getLogger(EventMessageReader::class.java)

        const val NEW_LINE = '\n'.toByte()

        val schema: CsvSchema = builder()
                .addColumn("time")
                .addColumn("trigger")
                .addColumn("action")
                .setLineSeparator(',')
                .build()

        fun DataBuffer.asCsvChunk(lastRow: AtomicReference<List<Byte>>) = asInputStream()
                .readAllBytes()
                .let { content ->
                    val cutIndex = content.lastIndexOf(NEW_LINE)
                    val (currentContent, nextPrefix) = if (cutIndex != content.lastIndex && cutIndex != -1) {
                        content.slice(0 until cutIndex) to content.slice(cutIndex..content.lastIndex)
                    } else {
                        content.toList() to emptyList()
                    }
                    val currentPrefix = lastRow.getAndSet(nextPrefix)
                    csvChunk(currentPrefix, currentContent)
                }.also { if (logger.isDebugEnabled) logger.debug(String(it)) }

        fun csvChunk(prefix: List<Byte>, content: List<Byte>) = (prefix + content).toByteArray()

        fun AtomicReference<List<Byte>>.toLastChunk(reader: ObjectReader) = Flux.defer {
            val data = get()
            if (data.isEmpty()) Flux.empty()
            else Mono.fromCallable {
                val lastRow = data.drop(1).toByteArray()
                if (logger.isDebugEnabled) logger.debug("last row: ${String(lastRow)}")
                reader.readValues<EventMessage>(lastRow)
            }.flatMapMany { it.toFlux() }
        }
    }
}
