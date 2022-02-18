package org.kate.examples.cars.websvc.outbound

import org.kate.domain.KateErrorImpact
import org.kate.domain.KateRequest
import org.kate.examples.cars.common.domain.CarAdviceRequest
import org.kate.outbound.KateSender
import org.springframework.stereotype.Component

interface CarOutHandler {
    fun submitRequestSellCarAdvice(requestId: String, requestBody : CarAdviceRequest)
    fun notifyTimeoutError(traceId: String, requestId: String, message: String)
}

@Component
class CarOutHandlerImpl(val kateSender: KateSender) : CarOutHandler{

    override fun submitRequestSellCarAdvice(requestId: String, requestBody : CarAdviceRequest) =
        kateSender.sendRequestMessage( KateRequest.create(id= requestId, traceId=requestId, requestBody=requestBody))

    override fun notifyTimeoutError(traceId: String, requestId: String, message: String) =
        kateSender.sendErrorMessage(traceId, requestId, KateErrorImpact.LOW, message, "Try again")

}
