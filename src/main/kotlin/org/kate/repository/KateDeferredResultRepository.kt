package org.kate.repository

import org.springframework.web.context.request.async.DeferredResult

interface KateDeferredResultRepository<T> {
    fun registerDeferredResult(requestId: String): DeferredResult<T>
    fun resolveDeferredResult(requestId: String, result: T)
}
