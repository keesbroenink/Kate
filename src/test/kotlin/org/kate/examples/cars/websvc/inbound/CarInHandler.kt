package org.kate.examples.cars.websvc.inbound

import org.kate.common.KateRequest
import org.kate.common.KateResponse
import org.kate.common.KateResponseReceivedCallback
import org.kate.examples.cars.common.domain.CarAdviceResponse
import org.kate.examples.cars.websvc.domain.CarService
import org.springframework.stereotype.Component
import org.kate.examples.cars.websvc.WebRequestResponseSynchronizer

@Component
class CarInHandler(val carService: CarService, val syncer: WebRequestResponseSynchronizer): KateResponseReceivedCallback<CarAdviceResponse> {

    override fun invoke(response: KateResponse, request: KateRequest){
        syncer.synchronize(response.requestId, response.responseBody as CarAdviceResponse)
    }


}
