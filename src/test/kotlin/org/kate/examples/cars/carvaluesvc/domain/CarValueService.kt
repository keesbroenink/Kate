package org.kate.examples.cars.carvaluesvc.domain

import org.kate.common.KateEvent
import org.kate.common.KateRequest
import org.kate.common.KateRequestReceivedCallback
import org.kate.common.KateResponse
import org.kate.common.outbound.kafka.KateKafkaSender
import org.kate.examples.cars.common.domain.CarValueRequest
import org.kate.examples.cars.common.domain.CarValueResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CarValueService (val kateKafkaSender: KateKafkaSender) : KateRequestReceivedCallback<CarValueRequest> {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(CarValueService::class.java)
    }

    override fun invoke(request: KateRequest) {
        val car = request.requestBody as CarValueRequest;
        val priceEuros = when  {
            car.yearBuilt < 2000  -> 1000
            car.yearBuilt == 2000 -> 1500
            else                  -> 2000
        }
        val responseBody = CarValueResponse(type = car.type, euros = priceEuros, yearBuilt = car.yearBuilt)

        // let's sleep a while to test the timeout mechanism
//        sleep(600)

        kateKafkaSender.sendReply(request, KateResponse.create(traceId = request.traceId, requestId=request.id, responseBody = responseBody))

        // for fun raise an event with the same body
        kateKafkaSender.sendEventMessage( KateEvent.create(traceId = request.traceId, eventBody = responseBody))
    }
}
