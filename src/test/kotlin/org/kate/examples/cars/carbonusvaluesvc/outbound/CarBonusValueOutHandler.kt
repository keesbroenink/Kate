package org.kate.examples.cars.carbonusvaluesvc.outbound

import org.kate.domain.KateRequest
import org.kate.domain.KateResponse
import org.kate.examples.cars.common.domain.CarBonusValueRequest
import org.kate.examples.cars.common.domain.CarBonusValueResponse
import org.kate.outbound.KateSender
import org.springframework.stereotype.Component

interface CarBonusValueOutHandler {
    fun sendBonusValue(request: KateRequest, bonus: Int)
}

@Component
class CarBonusValueOutHandlerImpl (val kateSender: KateSender) : CarBonusValueOutHandler {

    override fun sendBonusValue(request: KateRequest, bonus: Int) {
        val car = request.requestBody as CarBonusValueRequest;

        kateSender.sendReply(request,
            KateResponse.create(traceId = request.traceId, requestId = request.id,
                responseBody = CarBonusValueResponse(type = car.type, euros = bonus, yearBuilt = car.yearBuilt)
            )
        )
    }
}
