package org.kate.internal.repository.impl

import org.kate.repository.KateDeferredResultRepository
import org.springframework.stereotype.Component
import org.springframework.web.context.request.async.DeferredResult
import java.util.concurrent.ConcurrentHashMap


@Component
class KateDeferredResultMemoryRepository<T>() : KateDeferredResultRepository<T> {

    private val deferredResultMap = ConcurrentHashMap<String,Pair<Long,DeferredResult<T>>>()

    override fun registerDeferredResult(requestId: String, timeOutMs: Long) : Pair<Long,DeferredResult<T>>{
        deferredResultMap[requestId] = Pair(timeOutMs, DeferredResult<T>(timeOutMs))
        return deferredResultMap[requestId]!!
    }

    override fun getRemoveDeferredResult(requestId: String): Pair<Long,DeferredResult<T>>? = deferredResultMap[requestId].also {
        deferredResultMap.remove(requestId)
    }

}
