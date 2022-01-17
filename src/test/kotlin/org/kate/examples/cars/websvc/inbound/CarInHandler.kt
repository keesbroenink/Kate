package org.kate.examples.cars.websvc.inbound

import org.kate.common.KateRequest
import org.kate.common.KateResponse
import org.kate.common.KateResponseReceivedCallback
import org.kate.examples.cars.common.domain.SellCarResponse
import org.kate.examples.cars.websvc.domain.CarService
import org.springframework.stereotype.Component

@Component
class CarInHandler(val carService: CarService): KateResponseReceivedCallback<SellCarResponse> {

    override fun invoke(response: KateResponse, request: KateRequest) =
        carService.sellCarAdviceResult(response)

}
