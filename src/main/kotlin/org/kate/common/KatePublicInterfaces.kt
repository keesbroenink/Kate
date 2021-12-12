package org.kate.common

interface KateRequestBody
interface KateResponseBody
interface KateEventBody

interface KateRequestReceivedCallback<T: KateRequestBody>{
    fun getBodyType(bodyType: T) {}
    fun kateInvokeInternal(request: KateRequest) {
        invoke( request)
    }
    fun invoke(request: KateRequest)
}

interface KateResponseReceivedCallback<T: KateResponseBody>{
    fun getBodyType(bodyType: T) {}
    fun kateInvokeInternal(response: KateResponse, request: KateRequest) {
        invoke(response, request)
    }

    fun invoke(response: KateResponse, request: KateRequest)
}

interface KateMultipleResponsesReceivedCallback<T: KateResponseBody>{
    fun getBodyType(bodyType: T) {}
    fun kateInvokeInternal(response: KateResponse, request: KateRequest) {

    }

    fun invoke(responses: List<KateResponse>, originalRequest: KateRequest)
    fun numberResponses(): Int
}

interface KateEventReceivedCallback<T: KateEventBody> {
    fun getBodyType(bodyType: T) {}
    fun kateInvokeInternal(event: KateEvent) {
        invoke(event)
    }
    fun invoke(event: KateEvent)
}
