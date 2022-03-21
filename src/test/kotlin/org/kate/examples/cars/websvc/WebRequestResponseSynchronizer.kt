package org.kate.examples.cars.websvc

import org.kate.examples.cars.common.domain.CarAdviceResponse
import org.kate.examples.cars.websvc.in_outbound.ChatHandler
import org.kate.examples.cars.websvc.in_outbound.Message
import org.kate.examples.cars.websvc.outbound.CarOutHandler
import org.kate.repository.KateDeferredResultRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.context.request.async.DeferredResult

interface WebRequestResponseSynchronizer {
    fun registerRequest(traceId: String, requestId: String, timeOutMilliSeconds: Long): DeferredResult<CarAdviceResponse>
    fun synchronize(requestId: String, carAdviceResponse: CarAdviceResponse)
}

@Component
class WebRequestResponseSynchronizerImpl(val kateRepo: KateDeferredResultRepository<CarAdviceResponse>,
                                         val carOutHandler: CarOutHandler,
                                         val chatHandler: ChatHandler
):  WebRequestResponseSynchronizer{
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WebRequestResponseSynchronizerImpl::class.java)
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

    override fun synchronize(requestId: String, carAdviceResponse: CarAdviceResponse) {
        showcaseWebsocketChat(carAdviceResponse)
        val deferredResult = kateRepo.getRemoveDeferredResult(requestId) ?: return
        if (deferredResult.second.isSetOrExpired) {
            LOGGER.warn("Within the waiting period ${deferredResult.first}ms we received no result for request $requestId")
        } else {
            deferredResult.second.setResult(carAdviceResponse)
        }
    }

    private fun showcaseWebsocketChat(carAdviceResponse: CarAdviceResponse) {
        chatHandler.broadcast(Message(msgType = "say", data = carAdviceResponse.toString()))
    }
}