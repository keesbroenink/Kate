package org.kate.examples.cars.websvc.inbound

import org.kate.domain.KateRequest
import org.kate.domain.KateResponse
import org.kate.domain.KateResponseReceivedCallback
import org.kate.examples.cars.common.domain.CarAdviceResponse
import org.kate.examples.cars.websvc.WebRequestResponseSynchronizer
import org.kate.examples.cars.websvc.domain.CarService
import org.springframework.stereotype.Component

@Component
class CarInHandler(val carService: CarService, val syncer: WebRequestResponseSynchronizer): KateResponseReceivedCallback<CarAdviceResponse> {

    override fun invoke(response: KateResponse, request: KateRequest){
        syncer.synchronize(response.requestId, response.responseBody as CarAdviceResponse)
    }


}
