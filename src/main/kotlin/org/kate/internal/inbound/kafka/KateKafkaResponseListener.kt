package org.kate.internal.inbound.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.kate.domain.KateResponse
import org.kate.domain.KateResponseBody
import org.kate.domain.KateResponseReceivedCallback
import org.kate.internal.conversion.convertJsonToKateResponse
import org.kate.internal.conversion.deserializer
import org.kate.internal.repository.KateObjectNotFoundException
import org.kate.internal.repository.KatePrivateWriteRepository
import org.kate.repository.KateReadRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component

@Component
class KateKafkaResponseListener(private val kateRepo: KateReadRepository,
                                private val katePrivateRepo: KatePrivateWriteRepository,
                                private val kateResponseReceivedCallbacks: List<KateResponseReceivedCallback<out KateResponseBody>>) {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(KateKafkaResponseListener::class.java)
    }

    private val callbackMap = mutableMapOf<String, KateResponseReceivedCallback<out KateResponseBody>>()
    init {
        kateResponseReceivedCallbacks.map {
            callbackMap.put(getBodyClassname(it.javaClass.declaredMethods),it)
        }
    }

    @KafkaListener(containerFactory = "kateResponseListenerFactory", autoStartup = "\${kate.consumer.response.enabled:true}",
        topics = ["#{@KateInitialization.consumerResponseTopics}"] )
    private fun kateListener(
        cr: ConsumerRecord<String, String>,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String?,
        @Header(KafkaHeaders.OFFSET) offset: Long,
        ack: Acknowledgment
    ) {
        LOGGER.debug("=====> kate message received ${cr.value()} from topic $topic with offset $offset")

        try {
            // RESPONSE, first deserialize to kate framework fields
            val bareResponse = deserializer.readValue(cr.value(), KateResponse::class.java)
            // then deserialize body
            val callback = callbackMap[bareResponse.responseBodyType]
            if (callback != null) {
                val kateResponse = convertJsonToKateResponse(cr.value())
                try {
                    val kateRequest = kateRepo.getRequest(kateResponse.requestId)
                    // if we don't have the parent request it doesn't make sense to deliver responses
                    if (kateRequest.parentRequestId != null) {
                        kateRepo.getRequest(kateRequest.parentRequestId)//throws exception if not found
                    }
                    katePrivateRepo.saveKateResponseByRequestId(kateRequest, kateResponse)
                    KateKafkaRequestListener.LOGGER.info("RESPONSE RECEIVED $kateResponse")
                    callback.kateInvokeInternal(kateResponse, kateRequest)
                } catch (notfound: KateObjectNotFoundException) {}
            }
            ack.acknowledge()
        } catch (e: Exception) {
            LOGGER.error("=====> error ${e.message}")
        }

    }


}
