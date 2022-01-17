package org.kate.examples.cars.websvc.domain

import org.kate.common.KateResponse
import org.kate.examples.cars.common.domain.SellCarRequest
import org.kate.examples.cars.common.domain.SellCarResponse
import org.kate.examples.cars.websvc.outbound.CarOutHandler
import org.kate.repository.KateDeferredResultRepository
import org.springframework.stereotype.Component
import org.springframework.web.context.request.async.DeferredResult

@Component
class CarService(val carOutHandler: CarOutHandler, val kateRepo: KateDeferredResultRepository<SellCarResponse>) {

    fun sellCarAdvice(requestId: String, type: String, licensePlate: String, yearBuilt: Int, minimumPriceEuros: Int): DeferredResult<SellCarResponse> {
        carOutHandler.submitRequestSellCarAdvice(requestId, SellCarRequest(type = type, licensePlate = licensePlate, yearBuilt = yearBuilt, minimumPriceEuros= minimumPriceEuros))
        return kateRepo.registerDeferredResult(requestId, requestId,500)
    }

    fun sellCarAdviceResult(response: KateResponse) =
        kateRepo.resolveDeferredResult(response.requestId, response.requestId, response.responseBody as SellCarResponse)

}
