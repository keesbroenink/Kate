package org.kate.domain

import com.fasterxml.jackson.annotation.JsonProperty
import java.net.InetAddress
import java.time.Instant
import java.util.*

data class KateRequest private constructor(
    val modelVersion     : String = "1.0",
    val id               : String,
    val traceId          : String,
    val creationTimestamp: Instant = Instant.now(),
    val parentRequestId  : String? = null,
    val replyTopic       : String? = null,
    val requestBodyType  : String, //fully qualified class name
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var requestBody      : KateRequestBody?
) {
    companion object {
        fun create(id: String=UUID.randomUUID().toString(), traceId: String, creationTimestamp: Instant = Instant.now(),
                   parentRequestId: String?=null, replyTopic: String? = null,
                   requestBody: KateRequestBody) =
                        KateRequest(id=id, traceId = traceId, creationTimestamp = creationTimestamp,
                                    parentRequestId = parentRequestId, replyTopic = replyTopic,
                                    requestBodyType = requestBody.javaClass.canonicalName, requestBody = requestBody)
    }
}

data class KateResponse private constructor(
    val modelVersion     : String = "1.0",
    val id               : String,
    val traceId          : String,
    val creationTimestamp: Instant = Instant.now(),
    val requestId        : String,
    val responseBodyType : String, //fully qualified class name
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var responseBody     : KateResponseBody?
) {
    companion object {
        fun create(id: String=UUID.randomUUID().toString(), traceId: String, creationTimestamp: Instant = Instant.now(),
                   requestId: String, responseBody: KateResponseBody) =
            KateResponse(id=id, traceId = traceId, creationTimestamp = creationTimestamp, requestId = requestId,
                responseBodyType = responseBody.javaClass.canonicalName, responseBody = responseBody)
    }
}

data class KateEvent private constructor(
    val modelVersion     : String = "1.0",
    val id               : String,
    val traceId          : String,
    val creationTimestamp: Instant = Instant.now(),
    val eventBodyType    : String, //fully qualified class name
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var eventBody        : KateEventBody?
) {
    companion object {
        fun create(id: String=UUID.randomUUID().toString(), traceId: String, creationTimestamp: Instant = Instant.now(),
                   eventBody: KateEventBody) =
            KateEvent(id=id, traceId = traceId, creationTimestamp= creationTimestamp,
                   eventBodyType = eventBody.javaClass.canonicalName, eventBody = eventBody)
    }
}

enum class KateErrorImpact{LOW,MEDIUM,HIGH}
data class KateErrorMessageBody(val impact: KateErrorImpact, val errorDescription: String, val recoveryHint: String,
                                val relatedRequestId : String? = null, val relatedResponseId : String? = null,
                                val creationTimestamp: Instant = Instant.now(),
                                val hostName: InetAddress = InetAddress.getLocalHost()) : KateEventBody
