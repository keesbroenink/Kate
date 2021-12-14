package org.kate.repository.impl

import org.kate.common.KateRequest
import org.kate.common.KateRequestBody
import org.kate.common.KateResponse
import org.kate.common.KateResponseBody
import org.kate.common.conversion.convertJsonToKateRequest
import org.kate.common.conversion.convertJsonToKateResponse
import org.kate.repository.KatePrivateReadRepository
import org.kate.repository.KateReadRepository
import org.springframework.stereotype.Component

@Suppress("UNCHECKED_CAST")
@Component
class KateReadMemoryRepository(private val katePrivateRepository: KatePrivateReadRepository) : KateReadRepository{

    override fun getRequest(requestId: String): KateRequest {
        val requestJson = katePrivateRepository.getRequestJson(requestId)
        return convertJsonToKateRequest(requestJson)
    }

    override fun <T: KateRequestBody> getRequestBody(requestId: String, requiredClass: Class<T>): T? {
        val request = getRequest(requestId)
        if (request.requestBody != null && request.requestBody!!::class.java == requiredClass) {
            return request.requestBody as T
        }
        return null
    }

    override fun getResponseByRequestId(requestId: String): KateResponse {
        val responseJson = katePrivateRepository.getResponseJson(requestId)
        return convertJsonToKateResponse(responseJson)
    }

    override fun <T: KateResponseBody> getResponseBodyByRequestId(requestId: String, requiredClass: Class<T>): T? {
        val kateResponse = getResponseByRequestId(requestId)
        if (kateResponse.responseBody!!::class.java == requiredClass) {
            return kateResponse.responseBody as T
        }
        return null
    }

    override fun getResponsesByParentRequestId(parentRequestId: String): List<KateResponse> =
        katePrivateRepository.getResponsesParentRequestIdJson(parentRequestId).map {
            convertJsonToKateResponse(it)
        }

    override fun <T: KateResponseBody> findFirstResponseBodyByParentRequestId(parentRequestId: String, requiredClass: Class<T>): T? {
        for (kateResponse in getResponsesByParentRequestId(parentRequestId)) {
            if (kateResponse.responseBody != null && kateResponse.responseBody!!::class.java == requiredClass) {
                return kateResponse.responseBody as T
            }
        }
        return null
    }
}
