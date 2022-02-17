package org.kate.examples.cars.websvc.repository

import org.kate.examples.cars.common.domain.CarAdviceResponse
import org.kate.examples.cars.websvc.outbound.CarOutHandler
import org.kate.repository.KateDeferredResultRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.context.request.async.DeferredResult

interface WebRequestResponseRepository {
    fun registerRequest(traceId: String, requestId: String, timeOutMilliSeconds: Long): DeferredResult<CarAdviceResponse>
    fun resolveRequest(requestId: String, carAdviceResponse: CarAdviceResponse)
}

@Component
class WebRequestResponseRepositoryImpl(val kateRepo: KateDeferredResultRepository<CarAdviceResponse>,
                                       val carOutHandler: CarOutHandler
):  WebRequestResponseRepository{
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WebRequestResponseRepositoryImpl::class.java)
    }
    override fun registerRequest(traceId: String, requestId: String, timeOutMilliSeconds: Long): DeferredResult<CarAdviceResponse> {
        val deferredResult = kateRepo.registerDeferredResult( requestId, timeOutMilliSeconds)
        deferredResult.second.onTimeout {
            val errorStr = "Within the waiting period ${deferredResult.first}ms we received no result for request $requestId"
            carOutHandler.notifyTimeoutError(traceId, requestId, errorStr)
            deferredResult.second.setErrorResult(
                ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(errorStr)
            )
        }
        return deferredResult.second
    }

    override fun resolveRequest(requestId: String, carAdviceResponse: CarAdviceResponse) {
        val deferredResult = kateRepo.getRemoveDeferredResult(requestId) ?: return
        if (deferredResult.second.isSetOrExpired) {
            LOGGER.warn("Within the waiting period ${deferredResult.first}ms we received no result for request $requestId")
        } else {
            deferredResult.second.setResult(carAdviceResponse)
        }
    }
}