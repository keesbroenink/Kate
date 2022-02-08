package org.kate.examples.cars.caradvicesvc.domain

import org.kate.examples.cars.caradvicesvc.outbound.CarAdviceOutHandler
import org.kate.examples.cars.common.domain.CarBonusValueResponse
import org.kate.examples.cars.common.domain.CarValueResponse
import org.kate.examples.cars.common.domain.CarAdviceRequest
import org.kate.repository.KateReadRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

interface CarAdviceService {
    fun carAdvice(traceId: String, carAdviceRequestId: String, car: CarAdviceRequest)
    fun carAdviceResult(carAdviceRequestId: String)
}

@Component
class CarAdviceServiceImpl(val calculateCarAdviceService: CalculateCarAdviceService,
                           val kateRepo: KateReadRepository,
                           val carAdviceOutHandler: CarAdviceOutHandler) : CarAdviceService {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(CarAdviceService::class.java)
    }

    override fun carAdvice(traceId: String, carAdviceRequestId: String, car: CarAdviceRequest) {
        carAdviceOutHandler.askCarValueAndBonus(traceId, carAdviceRequestId, car)
    }

    override fun carAdviceResult(carAdviceRequestId: String) {
        val carAdviceRequest = kateRepo.getRequest(carAdviceRequestId)
        val carAdviceRequestBody = carAdviceRequest.requestBody as CarAdviceRequest

        val car = kateRepo.findFirstResponseBodyByParentRequestId(carAdviceRequestId, CarValueResponse::class.java)
            ?: return
        val bonus = kateRepo.findFirstResponseBodyByParentRequestId(carAdviceRequestId, CarBonusValueResponse::class.java)
            ?: return

        val result = calculateCarAdviceService.calculateCarAdvice(car.euros, bonus.euros, carAdviceRequestBody.minimumPriceEuros)
        LOGGER.info("Give advice $result for request $carAdviceRequestBody")

        carAdviceOutHandler.sendAdviceResult(carAdviceRequestBody, carAdviceRequest, result)
    }
}



