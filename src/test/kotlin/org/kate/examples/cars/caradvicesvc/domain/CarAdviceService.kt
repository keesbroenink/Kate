package org.kate.examples.cars.caradvicesvc.domain

import org.kate.examples.cars.caradvicesvc.outbound.CarAdviceOutHandler
import org.kate.examples.cars.common.domain.CarBonusValueResponse
import org.kate.examples.cars.common.domain.CarValueResponse
import org.kate.examples.cars.common.domain.SellAdvice
import org.kate.examples.cars.common.domain.SellCarRequest
import org.kate.repository.KateReadRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CarAdviceService(val kateRepo: KateReadRepository, val carAdviceOutHandler: CarAdviceOutHandler)  {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(CarAdviceService::class.java)
    }

    fun askCarValueAndBonus(traceId: String, initialRequestId: String, car: SellCarRequest) {
        carAdviceOutHandler.askCarValueAndBonus(traceId, initialRequestId, car)
    }

    fun adviceResult( parentRequestId: String) {
        val parentRequest = kateRepo.getRequest(parentRequestId)
        val sellCarRequest = parentRequest.requestBody as SellCarRequest

        val car = kateRepo.findFirstResponseBodyByParentRequestId(parentRequestId, CarValueResponse::class.java) ?: return
        val bonus = kateRepo.findFirstResponseBodyByParentRequestId(parentRequestId, CarBonusValueResponse::class.java) ?: return

        val result = if (car.euros + bonus.euros >= sellCarRequest.minimumPriceEuros) SellAdvice.SELL else SellAdvice.DONT_SELL
        LOGGER.info("Give advice $result for request $sellCarRequest")
        carAdviceOutHandler.sendAdviceResult(sellCarRequest, parentRequest, result)
    }
}



