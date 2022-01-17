package org.kate.examples.cars.caradvicesvc.outbound

import org.kate.common.KateRequest
import org.kate.common.KateResponse
import org.kate.common.outbound.kafka.KateKafkaSender
import org.kate.examples.cars.common.domain.*
import org.springframework.stereotype.Component

@Component
class CarAdviceOutHandler( val kateKafkaSender: KateKafkaSender)  {

    fun askCarValueAndBonus(traceId: String, initialRequestId: String, car: SellCarRequest) {
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

    fun sendAdviceResult( sellCarRequestBody: SellCarRequest, parentRequest: KateRequest, result: SellAdvice) {
        kateKafkaSender.sendReply( parentRequest, buildResponse(parentRequest.traceId, parentRequest.id, result, sellCarRequestBody) )
    }

    private fun buildResponse( traceId: String, parentRequestId: String, result: SellAdvice, carAdvice: SellCarRequest) =
        KateResponse.create( traceId = traceId, requestId = parentRequestId,
            responseBody = SellCarResponse(
                result, type = carAdvice.type, yearBuilt = carAdvice.yearBuilt,
                licensePlate = carAdvice.licensePlate, minimumPriceEuros = carAdvice.minimumPriceEuros
            )
        )
}


