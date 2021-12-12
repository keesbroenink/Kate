package org.kate.repository.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.context.request.async.DeferredResult
import java.util.concurrent.ConcurrentHashMap

const val TIME_OUT_MS:Long = 1000

@Component
class KateDeferredResultMemoryRepository<T>() : org.kate.repository.KateDeferredResultRepository<T> {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(KateDeferredResultMemoryRepository::class.java)
    }

    private val deferredResultMap = ConcurrentHashMap<String,DeferredResult<T>>()

    override fun registerDeferredResult(requestId: String): DeferredResult<T> {
        deferredResultMap[requestId] = DeferredResult<T>(TIME_OUT_MS)
        return deferredResultMap[requestId]!!
    }

    fun getDeferredResult(requestId: String): DeferredResult<T>? = deferredResultMap[requestId].also {
        deferredResultMap.remove(requestId)
    }

    override fun resolveDeferredResult(requestId: String, result: T) {
        val deferred = getDeferredResult(requestId) ?: return
        if (deferred.isSetOrExpired) {
            LOGGER.warn("Within the waiting period $TIME_OUT_MS (ms) we received no result for request ${requestId}")
        } else {
            deferred.setResult(result)
        }
    }

}
