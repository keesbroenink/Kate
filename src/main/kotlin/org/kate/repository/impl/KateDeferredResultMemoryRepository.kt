package org.kate.repository.impl

import org.kate.common.KateErrorImpact
import org.kate.common.outbound.kafka.KateKafkaSender
import org.kate.repository.KateDeferredResultRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.context.request.async.DeferredResult
import java.util.concurrent.ConcurrentHashMap


@Component
class KateDeferredResultMemoryRepository<T>(val kafkaSender: KateKafkaSender) : KateDeferredResultRepository<T> {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(KateDeferredResultMemoryRepository::class.java)
    }

    private val deferredResultMap = ConcurrentHashMap<String,Pair<Long,DeferredResult<T>>>()

    override fun registerDeferredResult(traceId: String, requestId: String, timeOutMs: Long): DeferredResult<T> {
        deferredResultMap[requestId] = Pair(timeOutMs, DeferredResult<T>(timeOutMs))
        val deferredResult = deferredResultMap[requestId]!!.second
        deferredResult.onTimeout {
            val errorStr = "Within the waiting period $timeOutMs ms we received no result for request $requestId"
            kafkaSender.sendErrorMessage(traceId, requestId, KateErrorImpact.LOW, errorStr, "Try again")
            deferredResult.setErrorResult(
                ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(errorStr)
            )
        }
        return deferredResult
    }

    private fun getRemoveDeferredResult(requestId: String): Pair<Long,DeferredResult<T>>? = deferredResultMap[requestId].also {
        deferredResultMap.remove(requestId)
    }

    override fun resolveDeferredResult(traceId: String, requestId: String, result: T) {
        val deferred = getRemoveDeferredResult(requestId) ?: return
        if (deferred.second.isSetOrExpired) {
            LOGGER.warn("Within the waiting period ${deferred.first}ms we received no result for request $requestId")
        } else {
            deferred.second.setResult(result)
        }
    }

}
