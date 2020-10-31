package eu.jrie.put.piper.piperhomeservice.infra.mapper

import com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser.Feature.SKIP_EMPTY_LINES
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import eu.jrie.put.piper.piperhomeservice.api.message.reader.EventMessageReader
import org.springframework.boot.web.codec.CodecCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary


@Configuration
class MapperConfig {
    @Bean
    fun csvMapper(): CsvMapper = CsvMapper().apply {
        registerModule(KotlinModule())
        enable(SKIP_EMPTY_LINES)
    }

    @Bean
    @Primary
    fun jsonMapper(): JsonMapper = JsonMapper().apply {
        registerModule(KotlinModule())
        registerModule(JavaTimeModule())
        disable(WRITE_DATES_AS_TIMESTAMPS)
    }

    @Bean
    fun customCodecCustomizer(eventMessageReader: EventMessageReader)= CodecCustomizer {
        it.customCodecs()
                .register(eventMessageReader)
    }
}
