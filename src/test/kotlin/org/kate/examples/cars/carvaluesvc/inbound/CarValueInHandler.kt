package org.kate.examples.cars.carvaluesvc.inbound

import org.kate.common.KateEvent
import org.kate.common.KateRequest
import org.kate.common.KateRequestReceivedCallback
import org.kate.common.KateResponse
import org.kate.common.outbound.kafka.KateKafkaSender
import org.kate.examples.cars.carvaluesvc.domain.CarValueService
import org.kate.examples.cars.carvaluesvc.outbound.CarValueOutHandler
import org.kate.examples.cars.common.domain.CarValueRequest
import org.kate.examples.cars.common.domain.CarValueResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CarValueInHandler (val carValueService: CarValueService,
                         val carValueOutHandler: CarValueOutHandler) : KateRequestReceivedCallback<CarValueRequest> {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(CarValueInHandler::class.java)
    }

    override fun invoke(request: KateRequest) {
        val car = request.requestBody as CarValueRequest;

        val priceEuros = carValueService.calculateValue(car.yearBuilt)

        carValueOutHandler.sendResult(request, priceEuros)

    }
}
