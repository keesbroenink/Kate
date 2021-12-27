package org.kate.examples.cars.carbonusvaluesvc.domain

import org.kate.common.KateRequest
import org.kate.common.KateRequestReceivedCallback
import org.kate.common.KateResponse
import org.kate.common.outbound.kafka.KateKafkaSender
import org.kate.examples.cars.common.domain.CarBonusValueRequest
import org.kate.examples.cars.common.domain.CarBonusValueResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CarBonusValueService (val kateKafkaSender: KateKafkaSender) : KateRequestReceivedCallback<CarBonusValueRequest> {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(CarBonusValueService::class.java)
    }

    override fun invoke(request: KateRequest) {
        val car = request.requestBody as CarBonusValueRequest;
        val bonus = when (car.type) {
            "TOYOTA" -> 500
            else     -> 0
        }
        kateKafkaSender.sendReply(request,
            KateResponse.create(traceId = request.traceId, requestId = request.id,
                responseBody = CarBonusValueResponse(type = car.type, euros = bonus, yearBuilt = car.yearBuilt)
            )
        )
    }
}
