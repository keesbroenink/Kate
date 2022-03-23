package org.kate.internal.repository.impl

import org.kate.domain.KateRequest
import org.kate.domain.KateRequestBody
import org.kate.domain.KateResponse
import org.kate.domain.KateResponseBody
import org.kate.internal.conversion.convertJsonToKateRequest
import org.kate.internal.conversion.convertJsonToKateResponse
import org.kate.internal.repository.KatePrivateReadRepository
import org.kate.repository.KateReadRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Suppress("UNCHECKED_CAST")
@Component
class KateReadMemoryRepository(private val katePrivateRepository: KatePrivateReadRepository) : KateReadRepository{
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(KateReadMemoryRepository::class.java)
    }
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
                LOGGER.debug("findFirstResponseBodyByParentRequestId $requiredClass found for parentId $parentRequestId")
                return kateResponse.responseBody as T
            }
        }
        return null
    }
}
