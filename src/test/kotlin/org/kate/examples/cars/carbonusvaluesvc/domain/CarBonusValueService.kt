package org.kate.examples.cars.carbonusvaluesvc.domain

import org.kate.examples.cars.common.domain.CarType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

interface CarBonusValueService {
    fun calculateValue(carType: CarType): Int
}

@Component
class CarBonusValueServiceImpl() : CarBonusValueService {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(CarBonusValueService::class.java)
    }

    override fun calculateValue(carType: CarType): Int {
        val carValue = when (carType) {
            CarType.TOYOTA -> 500
            else -> 0
        }
        LOGGER.info("Give car bonus value $carValue to car type $carType")
        return carValue
    }
}
