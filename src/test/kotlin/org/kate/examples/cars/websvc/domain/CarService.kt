package org.kate.examples.cars.websvc.domain

import org.kate.common.KateResponse
import org.kate.examples.cars.common.domain.CarAdviceRequest
import org.kate.examples.cars.common.domain.CarAdviceResponse
import org.kate.examples.cars.common.domain.CarType
import org.kate.examples.cars.websvc.outbound.CarOutHandler
import org.kate.repository.KateDeferredResultRepository
import org.springframework.stereotype.Component
import org.springframework.web.context.request.async.DeferredResult

interface CarService {
    fun sellCarAdvice(requestId: String, type: CarType, licensePlate: String, yearBuilt: Int, minimumPriceEuros: Int)
                        : DeferredResult<CarAdviceResponse>
    fun sellCarAdviceResult(response: KateResponse)
}

@Component
class CarServiceImpl(val carOutHandler: CarOutHandler,
                     val kateRepo: KateDeferredResultRepository<CarAdviceResponse>) : CarService{

    override fun sellCarAdvice(requestId: String, type: CarType, licensePlate: String, yearBuilt: Int, minimumPriceEuros: Int): DeferredResult<CarAdviceResponse> {
        carOutHandler.submitRequestSellCarAdvice(requestId, CarAdviceRequest(type = type, licensePlate = licensePlate, yearBuilt = yearBuilt, minimumPriceEuros= minimumPriceEuros))
        return kateRepo.registerDeferredResult(requestId, requestId,500)
    }

    override fun sellCarAdviceResult(response: KateResponse) =
        kateRepo.resolveDeferredResult(response.requestId, response.requestId, response.responseBody as CarAdviceResponse)

}
