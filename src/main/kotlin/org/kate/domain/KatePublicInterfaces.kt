package org.kate.domain

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

interface KateEventReceivedCallback<T: KateEventBody> {
    fun getBodyType(bodyType: T) {}
    fun kateInvokeInternal(event: KateEvent) {
        invoke(event)
    }
    fun invoke(event: KateEvent)
}
