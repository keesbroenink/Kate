package org.kate.examples.cars.carvaluesvc.outbound

import org.kate.common.KateEvent
import org.kate.common.KateRequest
import org.kate.common.KateResponse
import org.kate.common.outbound.kafka.KateKafkaSender
import org.kate.examples.cars.common.domain.CarValueRequest
import org.kate.examples.cars.common.domain.CarValueResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CarValueOutHandler (val kateKafkaSender: KateKafkaSender)  {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(CarValueOutHandler::class.java)
    }

    fun sendResult(request: KateRequest, priceEuros: Int) {
        val car = request.requestBody as CarValueRequest;
        val responseBody = CarValueResponse(type = car.type, euros = priceEuros, yearBuilt = car.yearBuilt)

        kateKafkaSender.sendReply(request, KateResponse.create(traceId = request.traceId, requestId=request.id, responseBody = responseBody))

        // for fun raise an event with the same body
        kateKafkaSender.sendEventMessage( KateEvent.create(traceId = request.traceId, eventBody = responseBody))
    }
}
