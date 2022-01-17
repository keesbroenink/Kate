package org.kate.examples.cars.carbonusvaluesvc.domain

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CarBonusValueService() {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(CarBonusValueService::class.java)
    }

    fun calculateValue(carType: String): Int {
        val carValue = when (carType) {
            "TOYOTA" -> 500
            else -> 0
        }
        LOGGER.info("Give car bonus value $carValue to car type $carType")
        return carValue
    }
}
