package org.kate.examples.cars

import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin

@Configuration
class CarsKafkaInitialization(@Value("\${kate.producer.bootstrap-servers}") val producerBootstrapServers: String,
                              @Value("\${kate.default-request-topic}")      val requestTopic: String,
                              @Value("\${kate.default-response-topic}")     val responseTopic: String,
                              @Value("\${kate.default-event-topic}")        val eventTopic: String,
                              @Value("\${kate.default-error-topic}")        val errorTopic: String,

) {
    private fun producerConfigs(): Map<String, Any> =  mapOf( ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to producerBootstrapServers)
    // ADMIN

    @Bean
    fun admin(): KafkaAdmin = KafkaAdmin(producerConfigs())

    @Bean
    fun topic1(): NewTopic = TopicBuilder.name(requestTopic).partitions(2).compact().build()
    @Bean
    fun topic2(): NewTopic = TopicBuilder.name(responseTopic).partitions(2).compact().build()
    @Bean
    fun topic3(): NewTopic = TopicBuilder.name(eventTopic).partitions(2).compact().build()
    @Bean
    fun topic4(): NewTopic = TopicBuilder.name(errorTopic).partitions(2).compact().build()

}
