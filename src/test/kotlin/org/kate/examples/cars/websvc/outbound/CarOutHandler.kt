package org.kate.examples.cars.websvc.outbound

import org.kate.common.KateErrorImpact
import org.kate.common.KateRequest
import org.kate.common.outbound.kafka.KateKafkaSender
import org.kate.examples.cars.common.domain.CarAdviceRequest
import org.springframework.stereotype.Component

interface CarOutHandler {
    fun submitRequestSellCarAdvice(requestId: String, requestBody : CarAdviceRequest)
    fun notifyTimeoutError(traceId: String, requestId: String, message: String)
}

@Component
class CarOutHandlerImpl(val kateKafkaSender: KateKafkaSender) : CarOutHandler{

    override fun submitRequestSellCarAdvice(requestId: String, requestBody : CarAdviceRequest) =
        kateKafkaSender.sendRequestMessage( KateRequest.create(id= requestId, traceId=requestId, requestBody=requestBody))

    override fun notifyTimeoutError(traceId: String, requestId: String, message: String) =
        kateKafkaSender.sendErrorMessage(traceId, requestId, KateErrorImpact.LOW, message, "Try again")

}
