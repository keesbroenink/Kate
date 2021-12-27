package org.kate.repository

import org.springframework.web.context.request.async.DeferredResult

const val TIME_OUT_MS:Long = 1000

interface KateDeferredResultRepository<T> {

    fun registerDeferredResult(traceId: String, requestId: String, timeOutMs: Long = TIME_OUT_MS): DeferredResult<T>
    fun resolveDeferredResult(traceId: String, requestId: String, result: T)
}
