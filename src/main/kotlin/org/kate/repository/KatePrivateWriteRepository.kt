package org.kate.repository

import org.kate.common.KateEvent
import org.kate.common.KateRequest
import org.kate.common.KateResponse

interface KatePrivateWriteRepository {
    fun saveKateEvent(event: KateEvent)
    fun saveKateRequest(request: KateRequest)
    fun saveKateResponseByRequestId(request: KateRequest, response: KateResponse)
}
