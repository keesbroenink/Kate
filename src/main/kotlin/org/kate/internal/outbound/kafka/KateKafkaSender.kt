package org.kate.internal.outbound.kafka

import org.kate.KateInitialization
import org.kate.domain.*
import org.kate.internal.conversion.convertKateEventToJson
import org.kate.internal.conversion.convertKateRequestToJson
import org.kate.internal.conversion.convertKateResponseToJson
import org.kate.internal.repository.KatePrivateWriteRepository
import org.kate.outbound.KateSender
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KateKafkaSender(private val kafkaTemplate: KafkaTemplate<String, String>,
                      private val repository: KatePrivateWriteRepository,
                      private val kateInitialization: KateInitialization
) : KateSender {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(KateKafkaSender::class.java)
    }
    override fun sendRequestMessage(kateRequest: KateRequest, topic: String? ) {
        val myTopic = topic ?: kateInitialization.producerRequestTopic
        val request = if (kateRequest.replyTopic==null)
                          kateRequest.copy(replyTopic = kateInitialization.producerResponseTopic)
                      else
                          kateRequest
        repository.saveKateRequest(request)
        kafkaTemplate.send(myTopic, request.traceId, convertKateRequestToJson(request))
    }
    /**
     * Use replyTopic in kateRequest.
     */
    override fun sendReply(kateRequest: KateRequest, responseObject: KateResponse) {
        if (kateRequest.replyTopic == null) throw IllegalArgumentException("The request's field replyTopic is not defined")
        repository.saveKateResponseByRequestId(kateRequest, responseObject)
        kafkaTemplate.send( kateRequest.replyTopic, responseObject.traceId, convertKateResponseToJson(responseObject))
    }

    /**
     * The replyTopic in kateRequest will be ignored.
     */
    override fun sendResponseMessage(kateRequest: KateRequest, kateResponse: KateResponse, topic: String? ) {
        val myTopic = topic ?: kateInitialization.producerResponseTopic
        repository.saveKateResponseByRequestId(kateRequest, kateResponse)
        kafkaTemplate.send( myTopic, kateResponse.traceId, convertKateResponseToJson(kateResponse))
    }

    override fun sendEventMessage(kateEvent: KateEvent, topic: String?) {
        val myTopic = topic ?: kateInitialization.producerEventTopic
        repository.saveKateEvent(kateEvent)
        kafkaTemplate.send( myTopic, kateEvent.traceId, convertKateEventToJson(kateEvent))
    }

    override fun sendErrorMessage(relatedTraceId: String, requestId: String, impact: KateErrorImpact, errorDescription: String,
                         recoveryHint: String, topic: String?) {
        val myTopic = topic ?: kateInitialization.producerErrorTopic
        LOGGER.error("$errorDescription. $recoveryHint" )
        val kateEvent = KateEvent.create(traceId= relatedTraceId,
            eventBody = KateErrorMessageBody(impact = impact, errorDescription = errorDescription, recoveryHint = recoveryHint, relatedRequestId = requestId)
        )
        kafkaTemplate.send( myTopic, relatedTraceId, convertKateEventToJson(kateEvent))
    }

}
