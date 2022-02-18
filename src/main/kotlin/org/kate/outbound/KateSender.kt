package org.kate.outbound

import org.kate.domain.KateErrorImpact
import org.kate.domain.KateEvent
import org.kate.domain.KateRequest
import org.kate.domain.KateResponse

interface KateSender {
    fun sendRequestMessage(kateRequest: KateRequest,topic: String? = null)
    fun sendReply(kateRequest: KateRequest, responseObject: KateResponse)
    fun sendResponseMessage(kateRequest: KateRequest, kateResponse: KateResponse, topic: String? = null)
    fun sendErrorMessage(traceId: String, requestId: String, kateErrorImpact: KateErrorImpact, message: String,
                         recoveryHint: String, topic: String? = null)
    fun sendEventMessage(kateEvent: KateEvent, topic: String? = null)
}
