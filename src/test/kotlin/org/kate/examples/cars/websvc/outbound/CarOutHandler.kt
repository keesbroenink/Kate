package org.kate.examples.cars.websvc.outbound

import org.kate.common.KateRequest
import org.kate.common.outbound.kafka.KateKafkaSender
import org.kate.examples.cars.common.domain.CarAdviceRequest
import org.springframework.stereotype.Component

@Component
class CarOutHandler(val kateKafkaSender: KateKafkaSender) {

    fun submitRequestSellCarAdvice(requestId: String, requestBody : CarAdviceRequest) =
        kateKafkaSender.sendRequestMessage( KateRequest.create(id= requestId, traceId=requestId, requestBody=requestBody))

}
