package org.kate.examples.cars.carvaluesvc.inbound

import org.kate.domain.KateRequest
import org.kate.domain.KateRequestReceivedCallback
import org.kate.examples.cars.carvaluesvc.domain.CarValueService
import org.kate.examples.cars.carvaluesvc.outbound.CarValueOutHandler
import org.kate.examples.cars.common.domain.CarValueRequest
import org.springframework.stereotype.Component

@Component
class CarValueInHandler (val carValueService: CarValueService,
                         val carValueOutHandler: CarValueOutHandler) : KateRequestReceivedCallback<CarValueRequest> {

    override fun invoke(request: KateRequest) {
        val car = request.requestBody as CarValueRequest;

        val priceEuros = carValueService.calculateValue(car.yearBuilt)

        carValueOutHandler.sendCarValue(request, priceEuros)

    }
}
