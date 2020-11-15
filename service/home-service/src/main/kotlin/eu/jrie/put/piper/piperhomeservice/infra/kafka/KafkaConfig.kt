package eu.jrie.put.piper.piperhomeservice.infra.kafka

import eu.jrie.put.piper.piperhomeservice.domain.model.NewModelEvent
import org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.kafka.support.serializer.JsonSerializer
import reactor.kafka.sender.SenderOptions
import reactor.kafka.sender.SenderOptions.create

@Configuration
class KafkaConfig {

    @Bean
    fun producerOptions(properties: KafkaProperties): SenderOptions<Int, NewModelEvent>
            = create((properties.buildProducerProperties() + additionalOptions))

    @Bean
    fun  modelMessageProducer(options: SenderOptions<Int, NewModelEvent>): ReactiveKafkaProducerTemplate<Int, NewModelEvent> {
        return ReactiveKafkaProducerTemplate(options)
    }

    private companion object {
        val additionalOptions: Map<String, Any> = mapOf(
                KEY_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
                VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java
        )
    }
}
