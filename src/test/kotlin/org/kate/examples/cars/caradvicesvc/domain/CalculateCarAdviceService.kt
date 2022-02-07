package org.kate.examples.cars.caradvicesvc.domain

import org.kate.examples.cars.common.domain.SellAdvice
import org.springframework.stereotype.Component

@Component
class CalculateCarAdviceService {
    fun calculateCarAdvice(carValue:Int, bonusValue: Int, minimumPriceEuros: Int) =
        if (carValue + bonusValue >= minimumPriceEuros) SellAdvice.SELL else SellAdvice.DONT_SELL


}