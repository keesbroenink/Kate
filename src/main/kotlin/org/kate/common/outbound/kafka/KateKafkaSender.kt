package org.kate.common.outbound.kafka

import org.kate.common.*
import org.kate.common.conversion.convertKateEventToJson
import org.kate.common.conversion.convertKateRequestToJson
import org.kate.common.conversion.convertKateResponseToJson
import org.kate.repository.KatePrivateWriteRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KateKafkaSender(private val kafkaTemplate: KafkaTemplate<String, String>,
                      private val repository: KatePrivateWriteRepository,
                      private val kateInitialization: KateInitialization) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(KateKafkaSender::class.java)
    }
    fun sendRequestMessage(kateRequest: KateRequest, topic: String=kateInitialization.producerRequestTopic ) {
        repository.saveKateRequest(kateRequest)
        kafkaTemplate.send(topic, kateRequest.traceId, convertKateRequestToJson(kateRequest))
    }

    fun sendReply(kateRequest: KateRequest, responseObject: KateResponse ) {
        if (kateRequest.replyTopic == null) throw IllegalArgumentException("The request's field replyTopic is not defined")
        repository.saveKateResponseByRequestId(kateRequest, responseObject)
        kafkaTemplate.send( kateRequest.replyTopic, responseObject.traceId, convertKateResponseToJson(responseObject))
    }

    /**
     * The replyTopic in kateRequest will be ignored.
     */
    fun sendResponseMessage(kateRequest: KateRequest, kateResponse: KateResponse, topic: String=kateInitialization.producerResponseTopic ) {
        repository.saveKateResponseByRequestId(kateRequest, kateResponse)
        kafkaTemplate.send( topic, kateResponse.traceId, convertKateResponseToJson(kateResponse))
    }

    fun sendEventMessage(kateEvent: KateEvent, topic: String=kateInitialization.producerEventTopic ) {
        repository.saveKateEvent(kateEvent)
        kafkaTemplate.send( topic, kateEvent.traceId, convertKateEventToJson(kateEvent))
    }

    fun sendErrorMessage(relatedTraceId: String, requestId: String, impact: KateErrorImpact, errorDescription: String,
                         recoveryHint: String, topic: String=kateInitialization.producerErrorTopic) {
        val kateEvent = KateEvent.create(traceId= relatedTraceId,
            eventBody = KateErrorMessageBody(impact = impact, errorDescription = errorDescription, recoveryHint = recoveryHint, relatedRequestId = requestId))
        kafkaTemplate.send( topic, relatedTraceId, convertKateEventToJson(kateEvent))
    }

}
