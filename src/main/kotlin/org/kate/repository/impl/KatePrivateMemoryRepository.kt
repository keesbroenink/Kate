package org.kate.repository.impl

import org.kate.common.KateEvent
import org.kate.common.KateRequest
import org.kate.common.KateResponse
import org.kate.common.conversion.convertKateEventToJson
import org.kate.common.conversion.convertKateRequestToJson
import org.kate.common.conversion.convertKateResponseToJson
import org.kate.repository.KateObjectNotFoundException
import org.kate.repository.KatePrivateReadRepository
import org.kate.repository.KatePrivateWriteRepository
import org.kate.repository.ObjectType
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
