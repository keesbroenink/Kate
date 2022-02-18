package org.kate.examples.cars.carvaluesvc.outbound

import org.kate.domain.KateEvent
import org.kate.domain.KateRequest
import org.kate.domain.KateResponse
import org.kate.examples.cars.common.domain.CarValueRequest
import org.kate.examples.cars.common.domain.CarValueResponse
import org.kate.outbound.KateSender
import org.springframework.stereotype.Component

interface CarValueOutHandler {
    fun sendCarValue(request: KateRequest, priceEuros: Int)
}

@Component
class CarValueOutHandlerImpl (val kateSender: KateSender) : CarValueOutHandler {

    override fun sendCarValue(request: KateRequest, priceEuros: Int) {
        val car = request.requestBody as CarValueRequest;
        val responseBody = CarValueResponse(type = car.type, euros = priceEuros, yearBuilt = car.yearBuilt)

        kateSender.sendReply(request, KateResponse.create(traceId = request.traceId, requestId=request.id, responseBody = responseBody))

        // for fun raise an event with the same body
        kateSender.sendEventMessage( KateEvent.create(traceId = request.traceId, eventBody = responseBody))
    }
}
