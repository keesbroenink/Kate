package org.kate.examples.cars.caradvicesvc.inbound

import org.kate.common.KateRequest
import org.kate.common.KateRequestReceivedCallback
import org.kate.common.KateResponse
import org.kate.common.KateResponseReceivedCallback
import org.kate.examples.cars.caradvicesvc.domain.CarAdviceService
import org.kate.examples.cars.common.domain.CarBonusValueResponse
import org.kate.examples.cars.common.domain.CarValueResponse
import org.kate.examples.cars.common.domain.CarAdviceRequest
import org.kate.repository.KateReadRepository
import org.springframework.stereotype.Component

@Component
class CarAdviceInHandler( val carAdviceService: CarAdviceService)  : KateRequestReceivedCallback<CarAdviceRequest> {
    override fun invoke(request: KateRequest) {
        carAdviceService.carAdvice(request.traceId, request.id, request.requestBody as CarAdviceRequest)
    }
}

@Component
class CarValueReceived( val kateRepo: KateReadRepository,
                        val carAdviceService: CarAdviceService) : KateResponseReceivedCallback<CarValueResponse> {
    override fun invoke(response: KateResponse, request: KateRequest) {
        request.parentRequestId?.apply {
            val orgRequest = kateRepo.getRequest(this)
            carAdviceService.carAdviceResult(this, orgRequest.requestBody as CarAdviceRequest)
        }
    }
}

@Component
class CarBonusValueReceived(val kateRepo: KateReadRepository,
                            val carAdviceService: CarAdviceService) : KateResponseReceivedCallback<CarBonusValueResponse> {
    override fun invoke(response: KateResponse, request: KateRequest) {
        request.parentRequestId?.apply {
            val orgRequest = kateRepo.getRequest(this)
            carAdviceService.carAdviceResult(this, orgRequest.requestBody as CarAdviceRequest)
        }
    }
}

