package org.kate.examples.cars.caradvicesvc.domain

import org.kate.examples.cars.caradvicesvc.outbound.CarAdviceOutHandler
import org.kate.examples.cars.caradvicesvc.repository.ResponseRepository
import org.kate.examples.cars.common.domain.CarAdviceRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

interface CarAdviceService {
    fun carAdvice(traceId: String, carAdviceRequestId: String, car: CarAdviceRequest)
    fun carAdviceResult(carAdviceRequestId: String, carAdviceRequestBody: CarAdviceRequest)
}

@Component
class CarAdviceServiceImpl(val calculateCarAdviceService: CalculateCarAdviceService,
                           val responseRepo: ResponseRepository,
                           val carAdviceOutHandler: CarAdviceOutHandler) : CarAdviceService {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(CarAdviceService::class.java)
    }

    override fun carAdvice(traceId: String, carAdviceRequestId: String, car: CarAdviceRequest) {
        carAdviceOutHandler.askCarValueAndBonus(traceId, carAdviceRequestId, car)
    }

    override fun carAdviceResult(carAdviceRequestId: String, carAdviceRequestBody: CarAdviceRequest) {
        val car = responseRepo.findCarValueResponse(carAdviceRequestId) ?: return
        val bonus = responseRepo.findCarBonusValueResponse(carAdviceRequestId) ?: return

        val result = calculateCarAdviceService.calculateCarAdvice(car.euros, bonus.euros, carAdviceRequestBody.minimumPriceEuros)
        LOGGER.info("Give advice $result for request $carAdviceRequestBody")

        carAdviceOutHandler.sendAdviceResult(carAdviceRequestId, carAdviceRequestBody, result)
    }
}



