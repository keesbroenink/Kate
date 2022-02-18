package org.kate.internal.repository

import org.kate.domain.KateEvent
import org.kate.domain.KateRequest
import org.kate.domain.KateResponse


interface KatePrivateWriteRepository {
    fun saveKateEvent(event: KateEvent)
    fun saveKateRequest(request: KateRequest)
    fun saveKateResponseByRequestId(request: KateRequest, response: KateResponse)
}
