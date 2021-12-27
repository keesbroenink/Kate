package org.kate.common.conversion

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.kate.common.*

val deserializer: ObjectMapper = ObjectMapper().registerModules(JavaTimeModule(), KotlinModule.Builder().build())
val serializer: ObjectMapper = deserializer.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

fun convertKateEventToJson(event: KateEvent): String = serializer.writeValueAsString(event)
fun convertKateRequestToJson(request: KateRequest): String = serializer.writeValueAsString(request)
fun convertKateResponseToJson(request: KateResponse): String = serializer.writeValueAsString(request)

private fun convertJsonToBareKateEvent(json: String): KateEvent = deserializer.readValue(json, KateEvent::class.java)
private fun convertJsonToBareKateRequest(json: String): KateRequest = deserializer.readValue(json, KateRequest::class.java)
private fun convertJsonToBareKateResponse(json: String): KateResponse = deserializer.readValue(json, KateResponse::class.java)

fun convertJsonToKateEvent(json: String) : KateEvent {
    val bareKateEvent = convertJsonToBareKateEvent(json)
    if (!json.contains("\"eventBody\":")) throw KateMalFormedObjectException()
    val body = json.substringAfter("\"eventBody\":").removeSuffix("}")
    val eventBody = deserializer.readValue(body, Class.forName(bareKateEvent.eventBodyType))
    bareKateEvent.eventBody = eventBody as KateEventBody
    return bareKateEvent
}

fun convertJsonToKateRequest(json: String) : KateRequest {
    val bareKateRequest = convertJsonToBareKateRequest(json)
    if (!json.contains("\"requestBody\":")) throw KateMalFormedObjectException()
    val body = json.substringAfter("\"requestBody\":").removeSuffix("}")
    val requestBody = deserializer.readValue(body, Class.forName(bareKateRequest.requestBodyType))
    bareKateRequest.requestBody = requestBody as KateRequestBody
    return bareKateRequest
}

fun convertJsonToKateResponse(json: String) : KateResponse {
    val bareKateResponse = convertJsonToBareKateResponse(json)
    if (!json.contains("\"responseBody\":")) throw KateMalFormedObjectException()
    val body = json.substringAfter("\"responseBody\":").removeSuffix("}")
    val responseBody = deserializer.readValue(body, Class.forName(bareKateResponse.responseBodyType))
    bareKateResponse.responseBody = responseBody as KateResponseBody
    return bareKateResponse
}
