package org.kate

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.kate.internal.util.OS
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.stereotype.Component


@Configuration
@ComponentScan("org.kate")
@PropertySource( "classpath:kate.yaml")
@Component("KateInitialization")
class KateInitialization(@Value("\${kate.consumer.bootstrap-servers}") val consumerBootstrapServers: String,
                         @Value("\${kate.producer.bootstrap-servers}") val producerBootstrapServers: String,
                         @Value("\${kate.producer.request-topic}")     val producerRequestTopic: String,
                         @Value("\${kate.producer.response-topic}")    val producerResponseTopic: String,
                         @Value("\${kate.producer.event-topic}")       val producerEventTopic: String,
                         @Value("\${kate.producer.error-topic}")       val producerErrorTopic: String,
                         @Value("\${kate.consumer.request-topics}")    val consumerRequestTopics: List<String>,
                         @Value("\${kate.consumer.response-topics}")   val consumerResponseTopics: List<String>,
                         @Value("\${kate.consumer.event-topics}")      val consumerEventTopics: List<String>,
                         @Value("\${kate.consumer.event-group-id}")    val eventGroupId: String,
                         @Value("\${kate.consumer.request-group-id}")  val requestGroupId: String,
                         @Value("\${kate.consumer.response-group-id}") val responseGroupId: String
) {

    //CONSUMER

    private fun buildConsumerGroupName(groupId: String, receiveAll: Boolean) =
        if (receiveAll) "$groupId-${OS.getCurrrentProcessId()}" else  groupId

    //CONSUMER requests

    private fun requestConsumerConfigs(): Map<String, Any> =
        mapOf( ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to consumerBootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to buildConsumerGroupName(requestGroupId, false)) //only one server should handle a request

    private fun kateRequestConsumerFactory(): ConsumerFactory<String, String> =
        DefaultKafkaConsumerFactory(
            requestConsumerConfigs(),
            StringDeserializer(),
            StringDeserializer()
        )

    @Bean
    fun kateRequestListenerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = kateRequestConsumerFactory()
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE
        return factory
    }

    //CONSUMER responses

    private fun responseConsumerConfigs(): Map<String, Any> =
        mapOf( ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to consumerBootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to buildConsumerGroupName(responseGroupId, true)) //responses should go to everyone (we don't know who received the request)

    private fun kateResponseConsumerFactory(): ConsumerFactory<String, String> =
        DefaultKafkaConsumerFactory(
            responseConsumerConfigs(),
            StringDeserializer(),
            StringDeserializer()
        )

    @Bean
    fun kateResponseListenerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = kateResponseConsumerFactory()
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE
        return factory
    }

    //CONSUMER events

    private fun eventConsumerConfigs(): Map<String, Any> =
        mapOf( ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to consumerBootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to buildConsumerGroupName(eventGroupId, true)) // all instances should always receive all events (design decision)


    private fun kateEventConsumerFactory(): ConsumerFactory<String, String> =
        DefaultKafkaConsumerFactory(
            eventConsumerConfigs(),
            StringDeserializer(),
            StringDeserializer()
        )

    @Bean
    fun kateEventListenerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = kateEventConsumerFactory()
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE
        return factory
    }

    // PRODUCER

    private fun producerConfigs(): Map<String, Any> =  mapOf( ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to producerBootstrapServers)

    private fun kateProducerFactory():ProducerFactory<String, String> =
        DefaultKafkaProducerFactory(
            producerConfigs(),
            StringSerializer(),
            StringSerializer()
        )

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(kateProducerFactory())
    }


}
