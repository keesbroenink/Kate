package org.kate.examples.cars.caradvicesvc.repository

import org.kate.examples.cars.common.domain.CarBonusValueResponse
import org.kate.examples.cars.common.domain.CarValueResponse
import org.kate.repository.KateReadRepository
import org.springframework.stereotype.Component

interface ResponseRepository {
    fun findCarValueResponse(carAdviceRequestId: String): CarValueResponse?
    fun findCarBonusValueResponse(carAdviceRequestId: String): CarBonusValueResponse?
}

@Component
class KateResponseRepositoryFacadeImpl(private val kateReadRepository: KateReadRepository): ResponseRepository {
    override fun findCarValueResponse(carAdviceRequestId: String): CarValueResponse? =
        kateReadRepository.findFirstResponseBodyByParentRequestId(carAdviceRequestId, CarValueResponse::class.java)

    override fun findCarBonusValueResponse(carAdviceRequestId: String): CarBonusValueResponse? =
        kateReadRepository.findFirstResponseBodyByParentRequestId(carAdviceRequestId, CarBonusValueResponse::class.java)

}