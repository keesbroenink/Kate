package org.kate.examples.cars.carbonusvaluesvc.inbound

import org.kate.domain.KateRequest
import org.kate.domain.KateRequestReceivedCallback
import org.kate.examples.cars.carbonusvaluesvc.domain.CarBonusValueService
import org.kate.examples.cars.carbonusvaluesvc.outbound.CarBonusValueOutHandler
import org.kate.examples.cars.common.domain.CarBonusValueRequest
import org.springframework.stereotype.Component

@Component
class CarBonusValueInHandler (val carBonusValueService: CarBonusValueService,
                              val carBonusValueOutHandler: CarBonusValueOutHandler) : KateRequestReceivedCallback<CarBonusValueRequest> {

    override fun invoke(request: KateRequest) {
        val car = request.requestBody as CarBonusValueRequest;
        val bonus = carBonusValueService.calculateValue(car.type)
        carBonusValueOutHandler.sendBonusValue(request, bonus)
    }
}
