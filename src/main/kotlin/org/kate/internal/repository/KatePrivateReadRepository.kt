package org.kate.internal.repository

interface KatePrivateReadRepository {
    fun getRequestJson(requestId: String) : String
    fun getEventJson(eventId: String) : String
    fun getResponseJson(requestId: String) : String
    fun getResponsesParentRequestIdJson(parentRequestId: String) : Set<String>
}
