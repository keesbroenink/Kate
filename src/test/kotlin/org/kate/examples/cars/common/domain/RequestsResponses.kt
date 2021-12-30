package org.kate.examples.cars.common.domain

import org.kate.common.KateEventBody
import org.kate.common.KateRequestBody
import org.kate.common.KateResponseBody

data class SellCarRequest(
    val type: String, val yearBuilt: Int,  val licensePlate: String, val minimumPriceEuros: Int
) : KateRequestBody

enum class SellAdvice{SELL,DONT_SELL}

data class SellCarResponse( val result: SellAdvice, val type: String, val yearBuilt: Int, val licensePlate: String, val minimumPriceEuros: Int
) : KateResponseBody

data class CarValueRequest( val type:String, val yearBuilt: Int): KateRequestBody
data class CarValueResponse(val type:String, val yearBuilt: Int, val euros: Int): KateResponseBody, KateEventBody

data class CarBonusValueRequest(val type:String, val yearBuilt: Int): KateRequestBody
data class CarBonusValueResponse(val type:String, val yearBuilt: Int, val euros: Int): KateResponseBody


