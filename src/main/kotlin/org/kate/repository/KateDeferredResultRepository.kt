package org.kate.repository

import org.springframework.web.context.request.async.DeferredResult

const val TIME_OUT_MS:Long = 1000

interface KateDeferredResultRepository<T> {

    fun registerDeferredResult(requestId: String, timeOutMs: Long = TIME_OUT_MS) : Pair<Long,DeferredResult<T>>
    fun getRemoveDeferredResult(requestId: String): Pair<Long,DeferredResult<T>>?
}
