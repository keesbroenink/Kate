package org.kate.examples.cars.common.domain

import org.kate.common.KateEventBody
import org.kate.common.KateRequestBody
import org.kate.common.KateResponseBody

data class SellCarRequest(
    val action: String = "SHOULD I SELL", val type: String, val yearBuilt: Int,  val licensePlate: String, val minimumPriceEuros: Int
) : KateRequestBody

data class SellCarResponse( val result: String, val type: String, val yearBuilt: Int, val licensePlate: String, val minimumPriceEuros: Int
) : KateResponseBody

data class CarValueRequest( val type:String, val yearBuilt: Int): KateRequestBody
data class CarValueResponse(val type:String, val yearBuilt: Int, val euros: Int): KateResponseBody, KateEventBody

data class CarBonusValueRequest(val type:String, val yearBuilt: Int): KateRequestBody
data class CarBonusValueResponse(val type:String, val yearBuilt: Int, val euros: Int): KateResponseBody


