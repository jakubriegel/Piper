package eu.jrie.put.piper.piperhomeservice.api

import eu.jrie.put.piper.piperhomeservice.api.message.EventMessage
import eu.jrie.put.piper.piperhomeservice.api.message.reader.EventMessageReader
import eu.jrie.put.piper.piperhomeservice.api.message.util.PiperMediaType.TEXT_CSV
import eu.jrie.put.piper.piperhomeservice.infra.mapper.MapperConfig
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.core.ResolvableType
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ReactiveHttpInputMessage
import org.springframework.http.codec.HttpMessageReader
import reactor.core.publisher.Flux

internal class EventMessageReaderTest {

    private val mapper = MapperConfig().csvMapper()
    private val converter: HttpMessageReader<EventMessage> = EventMessageReader(mapper)

    @Test
    fun `should return text_csv as readable media type`() {
        // expect
        assertIterableEquals(listOf(TEXT_CSV), converter.readableMediaTypes)
    }

    @Test
    fun `should return true for reading EventMessage as csv`() {
        // when
        val canRead = converter.canRead(type, TEXT_CSV)

        // then
        assertTrue(canRead)
    }

    @Test
    fun `should return false for reading EventMessage as json`() {
        // given
        val type = ResolvableType.forType(EventMessage::class.java)

        // when
        val canRead = converter.canRead(type, MediaType.APPLICATION_JSON)

        // then
        assertFalse(canRead)
    }

    @Test
    fun `should read single EventMessage`() = runBlocking {
        // given
        val eventCsv = "123456,trigger_1,action_1".toByteArray()
        val body = Flux.just(DefaultDataBufferFactory().wrap(eventCsv) as DataBuffer)
        val message = TestMessage(body)

        // when
        val result = converter.read(type, message, emptyMap())

        // then
        assertEquals(EventMessage("trigger_1", "action_1", "123456"), result.awaitSingle())
    }

    @Test
    fun `should read single EventMessage as Mono`() = runBlocking {
        // given
        val eventCsv = "123456,trigger_1,action_1".toByteArray()
        val body = Flux.just(DefaultDataBufferFactory().wrap(eventCsv) as DataBuffer)
        val message = TestMessage(body)

        // when
        val result = converter.readMono(type, message, emptyMap())

        // then
        assertEquals(EventMessage("trigger_1", "action_1", "123456"), result.awaitSingle())
    }

    @Test
    fun `should read chunked EventMessage csv`() = runBlocking {
        // given
        val firstChunk = """
            1589054854,kitchen_light_1_switch,light_on
            1589055809,kitchen_light_3
        """.trimIndent().toByteArray()
        val secondChunk = """
            _switch,light_on
            1589056736,kitchen_blind_1_switch,blind_up
            1589057233,kitchen_light_2_switch,light_on
        """.trimIndent().toByteArray()
        val chunks = listOf<DataBuffer>(
                DefaultDataBufferFactory().wrap(firstChunk),
                DefaultDataBufferFactory().wrap(secondChunk)
        )
        val body = Flux.fromIterable(chunks)
        val message = TestMessage(body)

        // when
        val result = converter.read(type, message, emptyMap())

        // then
        val expectedEvents = listOf(
                EventMessage("kitchen_light_1_switch", "light_on", "1589054854"),
                EventMessage("kitchen_light_3_switch", "light_on", "1589055809"),
                EventMessage("kitchen_blind_1_switch", "blind_up", "1589056736"),
                EventMessage("kitchen_light_2_switch", "light_on", "1589057233")
        )
        assertEquals(expectedEvents, result.collectList().awaitSingle())
    }

    private companion object {

        val type = ResolvableType.forType(EventMessage::class.java)

        class TestMessage(
                private val testBody: Flux<DataBuffer>
        ) : ReactiveHttpInputMessage {
            override fun getHeaders() = HttpHeaders.EMPTY
            override fun getBody() = testBody
        }
    }
}
