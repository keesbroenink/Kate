package org.kate.examples.cars.carbonusvaluesvc.outbound

import org.kate.common.KateRequest
import org.kate.common.KateResponse
import org.kate.common.outbound.kafka.KateKafkaSender
import org.kate.examples.cars.common.domain.CarBonusValueRequest
import org.kate.examples.cars.common.domain.CarBonusValueResponse
import org.springframework.stereotype.Component

interface CarBonusValueOutHandler {
    fun sendBonusValue(request: KateRequest, bonus: Int)
}

@Component
class CarBonusValueOutHandlerImpl (val kateKafkaSender: KateKafkaSender) : CarBonusValueOutHandler {

    override fun sendBonusValue(request: KateRequest, bonus: Int) {
        val car = request.requestBody as CarBonusValueRequest;

        kateKafkaSender.sendReply(request,
            KateResponse.create(traceId = request.traceId, requestId = request.id,
                responseBody = CarBonusValueResponse(type = car.type, euros = bonus, yearBuilt = car.yearBuilt)
            )
        )
    }
}
