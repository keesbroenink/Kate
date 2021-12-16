package org.kate.common.inbound.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.kate.common.KateRequest
import org.kate.common.KateRequestBody
import org.kate.common.KateRequestReceivedCallback
import org.kate.common.conversion.convertJsonToKateRequest
import org.kate.common.conversion.deserializer
import org.kate.repository.KatePrivateWriteRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component

@Component
class KateKafkaRequestListener(private val katePrivateRepo: KatePrivateWriteRepository,
                               private val kateRequestReceivedCallbacks: List<KateRequestReceivedCallback<out KateRequestBody>>) {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(KateKafkaRequestListener::class.java)
    }
    private val callbackMap = mutableMapOf<String, KateRequestReceivedCallback<out KateRequestBody>>()
    init {
        kateRequestReceivedCallbacks.map {
            callbackMap.put(getBodyClassname(it.javaClass.declaredMethods),it)
        }
    }

    @KafkaListener( containerFactory = "kateRequestListenerFactory", autoStartup = "\${kate.consumer.request.enabled:true}",
                    topics = ["#{@KateInitialization.consumerRequestTopics}"])
    private fun kateListener(
        cr: ConsumerRecord<String, String>,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String?,
        @Header(KafkaHeaders.OFFSET) offset: Long,
        ack: Acknowledgment
    ) {
        LOGGER.debug("=====> kate message received ${cr.value()} from topic $topic with offset $offset")

        try {
            // REQUEST, first deserialize to kate framework fields
            val bareRequest = deserializer.readValue(cr.value(), KateRequest::class.java)
            // then deserialize body
            val callback = callbackMap[bareRequest.requestBodyType]
            if (callback != null) {
                val kateRequest = convertJsonToKateRequest(cr.value())
                katePrivateRepo.saveKateRequest(kateRequest)
                LOGGER.info("REQUEST RECEIVED $kateRequest")
                callback.kateInvokeInternal(kateRequest)
            }
            ack.acknowledge() // we have to think hard what to do when an exception occurs; is it valid no never move the offset or are there certain cases where we must move on
        } catch (e: Exception) {
           LOGGER.error("=====> error ${e.message}")
        }

    }



//    @Bean
//    open fun kateRequestTopicBean() = topic

}
