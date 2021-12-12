package org.kate.repository

import org.kate.common.KateRequest
import org.kate.common.KateRequestBody
import org.kate.common.KateResponse
import org.kate.common.KateResponseBody

interface KateReadRepository {
    fun getRequest(requestId: String): KateRequest

    /**
     * Could return null if the body is not of the required class type
     */
    fun <T: KateRequestBody> getRequestBody(requestId: String, requiredClass: Class<T>): T?

    fun getResponseByRequestId(requestId: String): KateResponse

    /**
     * Could return null if the body is not of the required class type
     */
    fun <T: KateResponseBody> getResponseBodyByRequestId(requestId: String, requiredClass: Class<T>): T?

    fun getResponsesByParentRequestId(parentRequestId: String): List<KateResponse>

    /**
     * If you have more responses for this parentRequest with the same required class, you will get the first that
     * was found. TODO have other method to get them all
     */
    fun <T: KateResponseBody> findFirstResponseBodyByParentRequestId(parentRequestId: String, requiredClass: Class<T>): T?
}
