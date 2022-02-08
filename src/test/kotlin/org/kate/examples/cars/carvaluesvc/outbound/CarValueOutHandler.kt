package org.kate.examples.cars.carvaluesvc.outbound

import org.kate.common.KateEvent
import org.kate.common.KateRequest
import org.kate.common.KateResponse
import org.kate.common.outbound.kafka.KateKafkaSender
import org.kate.examples.cars.common.domain.CarValueRequest
import org.kate.examples.cars.common.domain.CarValueResponse
import org.springframework.stereotype.Component

interface CarValueOutHandler {
    fun sendCarValue(request: KateRequest, priceEuros: Int)
}

@Component
class CarValueOutHandlerImpl (val kateKafkaSender: KateKafkaSender) : CarValueOutHandler {

    override fun sendCarValue(request: KateRequest, priceEuros: Int) {
        val car = request.requestBody as CarValueRequest;
        val responseBody = CarValueResponse(type = car.type, euros = priceEuros, yearBuilt = car.yearBuilt)

        kateKafkaSender.sendReply(request, KateResponse.create(traceId = request.traceId, requestId=request.id, responseBody = responseBody))

        // for fun raise an event with the same body
        kateKafkaSender.sendEventMessage( KateEvent.create(traceId = request.traceId, eventBody = responseBody))
    }
}
