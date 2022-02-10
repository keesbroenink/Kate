package org.kate.examples.cars.caradvicesvc.outbound

import org.kate.common.KateRequest
import org.kate.common.KateResponse
import org.kate.common.outbound.kafka.KateKafkaSender
import org.kate.examples.cars.common.domain.*
import org.kate.repository.KateReadRepository
import org.springframework.stereotype.Component

interface CarAdviceOutHandler {
    fun askCarValueAndBonus(traceId: String, carAdviceRequestId: String, car: CarAdviceRequest)
    fun sendAdviceResult(carAdviceRequestId: String, carAdviceRequestBody: CarAdviceRequest, result: SellAdvice)
}

@Component
class CarAdviceOutHandlerImpl(val kateRepository: KateReadRepository, val kateKafkaSender: KateKafkaSender): CarAdviceOutHandler  {

    override fun askCarValueAndBonus(traceId: String, carAdviceRequestId: String, car: CarAdviceRequest) {
        kateKafkaSender.sendRequestMessage(
            KateRequest.create( traceId = traceId, parentRequestId = carAdviceRequestId,
                requestBody = CarValueRequest(type = car.type, yearBuilt = car.yearBuilt)
            )
        )
        kateKafkaSender.sendRequestMessage(
            KateRequest.create( traceId = traceId, parentRequestId = carAdviceRequestId,
                requestBody = CarBonusValueRequest(type = car.type, yearBuilt = car.yearBuilt)
            )
        )
    }

    override fun sendAdviceResult(carAdviceRequestId: String, carAdviceRequestBody: CarAdviceRequest, result: SellAdvice) {
        val kateRequest = kateRepository.getRequest(carAdviceRequestId)
        kateKafkaSender.sendReply( kateRequest, buildResponse(kateRequest.traceId, carAdviceRequestId, result, carAdviceRequestBody) )
    }

    private fun buildResponse( traceId: String, parentRequestId: String, result: SellAdvice, carAdvice: CarAdviceRequest) =
        KateResponse.create( traceId = traceId, requestId = parentRequestId,
            responseBody = CarAdviceResponse(
                result, type = carAdvice.type, yearBuilt = carAdvice.yearBuilt,
                licensePlate = carAdvice.licensePlate, minimumPriceEuros = carAdvice.minimumPriceEuros
            )
        )
}


