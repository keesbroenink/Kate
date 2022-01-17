package org.kate.examples.cars.caradvicesvc.inboud

import org.kate.common.KateRequest
import org.kate.common.KateRequestReceivedCallback
import org.kate.common.KateResponse
import org.kate.common.KateResponseReceivedCallback
import org.kate.examples.cars.caradvicesvc.domain.CarAdviceService
import org.kate.examples.cars.common.domain.CarBonusValueResponse
import org.kate.examples.cars.common.domain.CarValueResponse
import org.kate.examples.cars.common.domain.SellCarRequest
import org.springframework.stereotype.Component

@Component
class CarAdviceInHandler( val carAdviceService: CarAdviceService)  : KateRequestReceivedCallback<SellCarRequest> {
    override fun invoke(request: KateRequest) {
        carAdviceService.askCarValueAndBonus(request.traceId, request.id, request.requestBody as SellCarRequest)
    }
}

@Component
class CarValueReceived( val carAdviceService: CarAdviceService) : KateResponseReceivedCallback<CarValueResponse> {
    override fun invoke(response: KateResponse, request: KateRequest) {
        request.parentRequestId?.apply {
            carAdviceService.adviceResult(this)
        }
    }
}

@Component
class CarBonusValueReceived(val carAdviceService: CarAdviceService) : KateResponseReceivedCallback<CarBonusValueResponse> {
    override fun invoke(response: KateResponse, request: KateRequest) {
        request.parentRequestId?.apply {
            carAdviceService.adviceResult(this)
        }
    }
}

