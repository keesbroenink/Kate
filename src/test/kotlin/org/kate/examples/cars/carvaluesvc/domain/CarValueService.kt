package org.kate.examples.cars.carvaluesvc.domain

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

interface CarValueService {
    fun calculateValue(yearBuilt: Int): Int
}

@Component
class CarValueServiceImpl()  : CarValueService {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(CarValueService::class.java)
    }

    override fun calculateValue(yearBuilt: Int): Int {
       val carValue = when  {
            yearBuilt < 2000  -> 1000
            yearBuilt == 2000 -> 1500
            else              -> 2000
        }
        LOGGER.info("Give car value $carValue to car year built $yearBuilt")
        return carValue
    }

}
