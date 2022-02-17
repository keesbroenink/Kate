package org.kate.examples.cars.websvc.domain

import org.kate.examples.cars.common.domain.CarAdviceRequest
import org.kate.examples.cars.common.domain.CarType
import org.kate.examples.cars.websvc.outbound.CarOutHandler
import org.springframework.stereotype.Component

interface CarService {
    fun sellCarAdvice(requestId: String, type: CarType, licensePlate: String, yearBuilt: Int, minimumPriceEuros: Int)
}

@Component
class CarServiceImpl(val carOutHandler: CarOutHandler): CarService{

    override fun sellCarAdvice(requestId: String, type: CarType, licensePlate: String, yearBuilt: Int, minimumPriceEuros: Int) {
        carOutHandler.submitRequestSellCarAdvice(requestId, CarAdviceRequest(type = type, licensePlate = licensePlate, yearBuilt = yearBuilt, minimumPriceEuros= minimumPriceEuros))
    }

}
