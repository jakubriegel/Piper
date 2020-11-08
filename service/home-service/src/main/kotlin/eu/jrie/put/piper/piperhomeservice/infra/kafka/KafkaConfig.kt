package eu.jrie.put.piper.piperhomeservice.infra.kafka

import eu.jrie.put.piper.piperhomeservice.domain.model.ModelService
import org.apache.kafka.clients.producer.ProducerConfig.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.kafka.support.serializer.JsonSerializer
import reactor.kafka.sender.SenderOptions.create

@Configuration
class KafkaConfig {

    private fun producerOptions() = mapOf(
            BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
            CLIENT_ID_CONFIG to "home-service",
            ACKS_CONFIG to "all",
            KEY_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
            VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
    )

    @Bean
    fun  modelMessageProducer(): ReactiveKafkaProducerTemplate<Int, ModelService.NewModelMessage> {
        return ReactiveKafkaProducerTemplate(create(producerOptions()))
    }
}
