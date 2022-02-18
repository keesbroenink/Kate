package org.kate.examples.cars.caradvicesvc.outbound

import org.kate.domain.KateRequest
import org.kate.domain.KateResponse
import org.kate.examples.cars.common.domain.*
import org.kate.outbound.KateSender
import org.kate.repository.KateReadRepository
import org.springframework.stereotype.Component

interface CarAdviceOutHandler {
    fun askCarValueAndBonus(traceId: String, carAdviceRequestId: String, car: CarAdviceRequest)
    fun sendAdviceResult(carAdviceRequestId: String, carAdviceRequestBody: CarAdviceRequest, result: SellAdvice)
}

@Component
class CarAdviceOutHandlerImpl(val kateRepository: KateReadRepository, val kateSender: KateSender): CarAdviceOutHandler  {

    override fun askCarValueAndBonus(traceId: String, carAdviceRequestId: String, car: CarAdviceRequest) {
        kateSender.sendRequestMessage(
            KateRequest.create( traceId = traceId, parentRequestId = carAdviceRequestId,
                requestBody = CarValueRequest(type = car.type, yearBuilt = car.yearBuilt)
            )
        )
        kateSender.sendRequestMessage(
            KateRequest.create( traceId = traceId, parentRequestId = carAdviceRequestId,
                requestBody = CarBonusValueRequest(type = car.type, yearBuilt = car.yearBuilt)
            )
        )
    }

    override fun sendAdviceResult(carAdviceRequestId: String, carAdviceRequestBody: CarAdviceRequest, result: SellAdvice) {
        val kateRequest = kateRepository.getRequest(carAdviceRequestId)
        kateSender.sendReply( kateRequest, buildResponse(kateRequest.traceId, carAdviceRequestId, result, carAdviceRequestBody) )
    }

    private fun buildResponse( traceId: String, parentRequestId: String, result: SellAdvice, carAdvice: CarAdviceRequest) =
        KateResponse.create( traceId = traceId, requestId = parentRequestId,
            responseBody = CarAdviceResponse(
                result, type = carAdvice.type, yearBuilt = carAdvice.yearBuilt,
                licensePlate = carAdvice.licensePlate, minimumPriceEuros = carAdvice.minimumPriceEuros
            )
        )
}


