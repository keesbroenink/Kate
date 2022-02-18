package org.kate.internal.repository.impl

import org.kate.domain.KateEvent
import org.kate.domain.KateRequest
import org.kate.domain.KateResponse
import org.kate.internal.conversion.convertKateEventToJson
import org.kate.internal.conversion.convertKateRequestToJson
import org.kate.internal.conversion.convertKateResponseToJson
import org.kate.internal.repository.KateObjectNotFoundException
import org.kate.internal.repository.KatePrivateReadRepository
import org.kate.internal.repository.KatePrivateWriteRepository
import org.kate.internal.repository.ObjectType
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

@Component
class KatePrivateMemoryRepository() : KatePrivateWriteRepository, KatePrivateReadRepository {
    private val kateObjectMap = ConcurrentHashMap<String, String>()
    private val kateResponseMap = ConcurrentHashMap<String, String>()
    private val kateResponseByParentRequestMap = ConcurrentHashMap<String, MutableList<String>>()

    override fun saveKateEvent(event: KateEvent) {
        kateObjectMap[event.id] = convertKateEventToJson(event)
    }

    override fun saveKateRequest(request: KateRequest) {
        kateObjectMap[request.id] = convertKateRequestToJson(request)
    }

    override fun saveKateResponseByRequestId(request: KateRequest, response: KateResponse) {
        kateResponseMap[request.id] = convertKateResponseToJson(response)
        if (request.parentRequestId != null) {
            val jsonList = kateResponseByParentRequestMap[request.parentRequestId] ?: CopyOnWriteArrayList()
            jsonList.add(kateResponseMap[request.id]!!)
            kateResponseByParentRequestMap[request.parentRequestId] = jsonList
        }
    }

    override fun getRequestJson(requestId: String)  = kateObjectMap[requestId] ?: throw KateObjectNotFoundException(requestId, ObjectType.REQUEST)
    override fun getEventJson(eventId: String)      = kateObjectMap[eventId] ?: throw KateObjectNotFoundException(eventId, ObjectType.EVENT)
    override fun getResponseJson(requestId: String) = kateResponseMap[requestId] ?: throw KateObjectNotFoundException(requestId, ObjectType.RESPONSE_FOR_REQUEST)
    override fun getResponsesParentRequestIdJson(parentRequestId: String) = kateResponseByParentRequestMap[parentRequestId] ?: listOf()

}
