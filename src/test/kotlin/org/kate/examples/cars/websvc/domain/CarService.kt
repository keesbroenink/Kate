package org.kate.examples.cars.websvc.domain

import org.kate.common.KateRequest
import org.kate.common.KateResponse
import org.kate.common.KateResponseReceivedCallback
import org.kate.common.outbound.kafka.KateKafkaSender
import org.kate.examples.cars.common.domain.SellCarRequest
import org.kate.examples.cars.common.domain.SellCarResponse
import org.kate.repository.KateDeferredResultRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.context.request.async.DeferredResult

@Component
class CarService(@Value("\${kate.default-response-topic}") val replyTopic: String,//just for the example
                 val kateKafkaSender: KateKafkaSender,
                 val kateRepo: KateDeferredResultRepository<SellCarResponse>
): KateResponseReceivedCallback<SellCarResponse> {

    fun sellCarAdvice(requestId: String, type: String, licensePlate: String, yearBuilt: Int, minimumPriceEuros: Int): DeferredResult<SellCarResponse> {
        submitRequestSellCarAdvice(requestId, SellCarRequest(type = type, licensePlate = licensePlate, yearBuilt = yearBuilt, minimumPriceEuros= minimumPriceEuros))
        return kateRepo.registerDeferredResult(requestId, requestId,500)
    }

    private fun submitRequestSellCarAdvice(requestId: String, requestBody : SellCarRequest) =
        kateKafkaSender.sendRequestMessage( KateRequest.create(id= requestId, traceId=requestId,
            replyTopic = replyTopic, // can be removed because the default is ${kate.default-response-topic}
            requestBody=requestBody))

    override fun invoke(response: KateResponse, request: KateRequest) =
        kateRepo.resolveDeferredResult(response.requestId, response.requestId, response.responseBody as SellCarResponse)

}
