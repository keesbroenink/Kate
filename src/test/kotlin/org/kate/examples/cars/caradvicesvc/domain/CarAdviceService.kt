package org.kate.examples.cars.caradvicesvc.domain

import org.kate.common.KateRequest
import org.kate.common.KateRequestReceivedCallback
import org.kate.common.KateResponse
import org.kate.common.KateResponseReceivedCallback
import org.kate.common.outbound.kafka.KateKafkaSender
import org.kate.examples.cars.common.domain.*
import org.kate.repository.KateReadRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CarAdviceService( val kateKafkaSender: KateKafkaSender
)  : KateRequestReceivedCallback<SellCarRequest> {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(CarAdviceService::class.java)
    }

    override fun invoke(request: KateRequest) {
        askValueAndBonus(request.traceId, request.id, request.requestBody as SellCarRequest)
    }

    private fun askValueAndBonus(traceId: String, initialRequestId: String, car: SellCarRequest) {
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
}

@Component
class CalculateResult(val sender: KateKafkaSender, val kateRepo: KateReadRepository) {

    fun adviceResult( traceId: String, parentRequestId: String) {
        val parentRequest = kateRepo.getRequest(parentRequestId)
        val sellCarRequest = parentRequest.requestBody as SellCarRequest

        val car = kateRepo.findFirstResponseBodyByParentRequestId(parentRequestId, CarValueResponse::class.java) ?: return
        val bonus = kateRepo.findFirstResponseBodyByParentRequestId(parentRequestId, CarBonusValueResponse::class.java) ?: return

        val result = if (car.euros + bonus.euros >= sellCarRequest.minimumPriceEuros) SellAdvice.SELL else SellAdvice.DONT_SELL

        sender.sendReply( parentRequest, buildResponse(traceId, parentRequestId, result, sellCarRequest) )
    }

    private fun buildResponse( traceId: String, parentRequestId: String, result: SellAdvice, carAdvice: SellCarRequest) =
        KateResponse.create( traceId = traceId, requestId = parentRequestId,
            responseBody = SellCarResponse(
                result, type = carAdvice.type, yearBuilt = carAdvice.yearBuilt,
                licensePlate = carAdvice.licensePlate, minimumPriceEuros = carAdvice.minimumPriceEuros
            )
        )
}

@Component
class CarValueReceived( val calculate: CalculateResult) : KateResponseReceivedCallback<CarValueResponse> {
    override fun invoke(response: KateResponse, request: KateRequest) {
        if (request.parentRequestId == null) return
        calculate.adviceResult(request.traceId, request.parentRequestId!!)
    }
}

@Component
class CarBonusValueReceived(val calculate: CalculateResult) : KateResponseReceivedCallback<CarBonusValueResponse> {
    override fun invoke(response: KateResponse, request: KateRequest) {
        if (request.parentRequestId == null) return
        calculate.adviceResult(request.traceId, request.parentRequestId!!)
    }
}

