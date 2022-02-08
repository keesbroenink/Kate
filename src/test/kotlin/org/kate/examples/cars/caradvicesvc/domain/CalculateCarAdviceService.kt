package org.kate.examples.cars.caradvicesvc.domain

import org.kate.examples.cars.common.domain.SellAdvice
import org.springframework.stereotype.Component


interface CalculateCarAdviceService {
    fun calculateCarAdvice(carValue:Int, bonusValue: Int, minimumPriceEuros: Int): SellAdvice
}

@Component
class CalculateCarAdviceServiceImpl: CalculateCarAdviceService {
    override fun calculateCarAdvice(carValue:Int, bonusValue: Int, minimumPriceEuros: Int) =
        if (carValue + bonusValue >= minimumPriceEuros) SellAdvice.SELL else SellAdvice.DONT_SELL

}