package org.kate.examples.cars.common.domain

import org.kate.domain.KateEventBody
import org.kate.domain.KateRequestBody
import org.kate.domain.KateResponseBody

data class CarAdviceRequest(
    val type: CarType, val yearBuilt: Int,  val licensePlate: String, val minimumPriceEuros: Int
) : KateRequestBody

enum class SellAdvice{SELL,DONT_SELL}
enum class CarType{PEUGEOT, CITROEN, MAZDA, MERCEDES, BMW, OPEL, SKODA, NISSAN, TOYOTA, VOLKSWAGEN }

data class CarAdviceResponse(val result: SellAdvice, val type: CarType, val yearBuilt: Int, val licensePlate: String, val minimumPriceEuros: Int
) : KateResponseBody

data class CarValueRequest( val type:CarType, val yearBuilt: Int): KateRequestBody
data class CarValueResponse(val type:CarType, val yearBuilt: Int, val euros: Int): KateResponseBody, KateEventBody

data class CarBonusValueRequest(val type:CarType, val yearBuilt: Int): KateRequestBody
data class CarBonusValueResponse(val type:CarType, val yearBuilt: Int, val euros: Int): KateResponseBody


