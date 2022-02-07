package org.kate.examples.cars.websvc.inbound

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.kate.examples.cars.common.domain.CarType
import org.kate.examples.cars.websvc.domain.CarService
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/cars")
@Tag( name = "/api/cars", description = "Acting on cars")
internal class CarRestController(private val service: CarService) {

    @PutMapping("/adviceSellCar")
    @Operation(summary = "Should I sell my car?", description="details")
    fun sellCarAdvice(type: CarType, licensePlate: String, yearBuilt: Int, minimumPriceEuros: Int) =
        service.sellCarAdvice( UUID.randomUUID().toString(), type, licensePlate.uppercase(), yearBuilt, minimumPriceEuros)

}
