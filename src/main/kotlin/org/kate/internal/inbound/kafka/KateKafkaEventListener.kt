package org.kate.internal.inbound.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.kate.domain.KateEvent
import org.kate.domain.KateEventBody
import org.kate.domain.KateEventReceivedCallback
import org.kate.internal.conversion.convertJsonToKateEvent
import org.kate.internal.conversion.deserializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component


@Component
class KateKafkaEventListener(val kateRequestReceivedCallbacks: List<KateEventReceivedCallback<out KateEventBody>>) {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(KateKafkaEventListener::class.java)
    }
    private val callbackMap = mutableMapOf<String, KateEventReceivedCallback<out KateEventBody>>()
    init {
        kateRequestReceivedCallbacks.map {
            callbackMap.put(getBodyClassname(it.javaClass.declaredMethods),it)
        }
    }

    @KafkaListener(containerFactory = "kateEventListenerFactory", autoStartup = "\${kate.consumer.event.enabled:true}",
        topics = ["#{@KateInitialization.consumerEventTopics}"] )
    private fun kateListener(
        cr: ConsumerRecord<String, String>,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String?,
        @Header(KafkaHeaders.OFFSET) offset: Long,
        ack: Acknowledgment
    ) {
        LOGGER.debug("=====> kate message received ${cr.value()} from topic $topic with offset $offset")

        try {
            // EVENT, first deserialize to kate framework fields
            val bareEvent = deserializer.readValue(cr.value(), KateEvent::class.java)
            // then deserialize body
            val callback = callbackMap[bareEvent.eventBodyType]
            if (callback != null) {
                val kateEvent = convertJsonToKateEvent(cr.value())
                LOGGER.info("EVENT RECEIVED $kateEvent")
                callback.kateInvokeInternal(kateEvent)
            }
            ack.acknowledge()
        } catch (e: Exception) {
           LOGGER.error("=====> error ${e.message}")
        }

    }



//    @Bean
//    open fun kateRequestTopicBean() = topic

}
