package org.kate.examples.cars.websvc.inbound

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.kate.examples.cars.common.domain.CarAdviceResponse
import org.kate.examples.cars.common.domain.CarType
import org.kate.examples.cars.websvc.WebRequestResponseSynchronizer
import org.kate.examples.cars.websvc.domain.CarService
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.async.DeferredResult
import java.util.*

@RestController
@RequestMapping("/api/cars")
@Tag( name = "/api/cars", description = "Acting on cars")
class CarRestController(private val service: CarService, val syncer: WebRequestResponseSynchronizer) {

    @PutMapping("/adviceSellCar")
    @Operation(summary = "Should I sell my car?", description="details")
    fun sellCarAdvice(type: CarType, licensePlate: String, yearBuilt: Int, minimumPriceEuros: Int): DeferredResult<CarAdviceResponse> {
        val requestId = UUID.randomUUID().toString()
        val deferredResult = syncer.registerRequest(requestId, requestId, 500)
        service.sellCarAdvice(requestId, type,  licensePlate.uppercase(), yearBuilt, minimumPriceEuros)
        return deferredResult
    }
}
