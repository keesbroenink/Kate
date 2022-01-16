package org.kate.examples.cars.carvaluesvc.domain

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CarValueService()  {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(CarValueService::class.java)
    }

    fun calculateValue(yearBuilt: Int) =
       when  {
            yearBuilt < 2000  -> 1000
            yearBuilt == 2000 -> 1500
            else              -> 2000
        }


}
