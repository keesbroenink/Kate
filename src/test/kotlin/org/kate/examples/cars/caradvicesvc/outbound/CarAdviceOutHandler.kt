package org.kate.examples.cars.caradvicesvc.outbound

import org.kate.common.KateRequest
import org.kate.common.KateResponse
import org.kate.common.outbound.kafka.KateKafkaSender
import org.kate.examples.cars.common.domain.*
import org.springframework.stereotype.Component

@Component
class CarAdviceOutHandler( val kateKafkaSender: KateKafkaSender)  {

    fun askCarValueAndBonus(traceId: String, initialRequestId: String, car: CarAdviceRequest) {
        kateKafkaSender.sendRequestMessage(
            KateRequest.create( traceId = traceId, parentRequestId = initialRequestId,
                requestBody = CarValueRequest(type = car.type, yearBuilt = car.yearBuilt)
            )
        )
        kateKafkaSender.sendRequestMessage(
            KateRequest.create( traceId = traceId, parentRequestId = initialRequestId,
                requestBody = CarBonusValueRequest(type = car.type, yearBuilt = car.yearBuilt)
            )
        )
    }

    fun sendAdviceResult(carAdviceRequestBody: CarAdviceRequest, carAdviceRequest: KateRequest, result: SellAdvice) {
        kateKafkaSender.sendReply( carAdviceRequest, buildResponse(carAdviceRequest.traceId, carAdviceRequest.id, result, carAdviceRequestBody) )
    }

    private fun buildResponse( traceId: String, parentRequestId: String, result: SellAdvice, carAdvice: CarAdviceRequest) =
        KateResponse.create( traceId = traceId, requestId = parentRequestId,
            responseBody = CarAdviceResponse(
                result, type = carAdvice.type, yearBuilt = carAdvice.yearBuilt,
                licensePlate = carAdvice.licensePlate, minimumPriceEuros = carAdvice.minimumPriceEuros
            )
        )
}


