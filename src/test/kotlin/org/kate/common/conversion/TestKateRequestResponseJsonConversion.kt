package org.kate.common.conversion

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.kate.common.*
import java.time.Instant


class TestKateRequestResponseJsonConversion {

    @Test
    fun test_convertKateRequestToJson() {
       Assertions.assertEquals(JSON_REQUEST, convertKateRequestToJson(kateRequest))
    }
    @Test
    fun test_convertJsonToKateRequest() {
        Assertions.assertEquals(kateRequest, convertJsonToKateRequest(JSON_REQUEST))
    }
    @Test
    fun test_convertJsonToKateRequest_malformed() {
        try {
            convertJsonToKateRequest(JSON_REQUEST_WITHOUT_BODY)
            Assertions.fail("Expected exception ${KateMalFormedObjectException::class.java.canonicalName}")
        } catch (e: KateMalFormedObjectException) {
        } catch (other: Exception) {
            Assertions.fail("Wrong exception $other")
        }
    }

    @Test
    fun test_convertKateResponseToJson() {
        Assertions.assertEquals(JSON_RESPONSE, convertKateResponseToJson(kateResponse))
    }
    @Test
    fun test_convertJsonToKateResponse() {
        Assertions.assertEquals(kateResponse, convertJsonToKateResponse(JSON_RESPONSE))
    }
    @Test
    fun test_convertJsonToKateResponse_malformed() {
        try {
            convertJsonToKateResponse(JSON_RESPONSE_WITHOUT_BODY)
            Assertions.fail("Expected exception ${KateMalFormedObjectException::class.java.canonicalName}")
        } catch (e: KateMalFormedObjectException) {
        } catch (other: Exception) {
            Assertions.fail("Wrong exception $other")
        }
    }

    @Test
    fun test_convertKateEventToJson() {
        Assertions.assertEquals(JSON_EVENT, convertKateEventToJson(kateEvent))
    }
    @Test
    fun test_convertJsonToKateEvent() {
        Assertions.assertEquals(kateEvent, convertJsonToKateEvent(JSON_EVENT))
    }
    @Test
    fun test_convertJsonToKateEvent_malformed() {
        try {
            convertJsonToKateEvent(JSON_EVENT_WITHOUT_BODY)
            Assertions.fail("Expected exception ${KateMalFormedObjectException::class.java.canonicalName}")
        } catch (e: KateMalFormedObjectException) {
        } catch (other: Exception) {
            Assertions.fail("Wrong exception $other")
        }
    }

}
const val TIMESTAMP = "2021-12-27T09:57:35.503Z"

val kateRequest = KateRequest.create("test-id", "trace-id", creationTimestamp = Instant.parse(TIMESTAMP), requestBody=TestRequestBody())
val kateResponse = KateResponse.create("test-id", "trace-id", creationTimestamp = Instant.parse(TIMESTAMP), requestId="1-1", responseBody=TestResponseBody())
val kateEvent = KateEvent.create("test-id", "trace-id", creationTimestamp = Instant.parse(TIMESTAMP), eventBody=TestEventBody())

data class TestRequestBody(val field1: String = "test") : KateRequestBody
data class TestResponseBody(val field1: String = "test") : KateResponseBody
data class TestEventBody(val field1: String = "test") : KateEventBody

const val JSON_EVENT = """{"modelVersion":"1.0","id":"test-id","traceId":"trace-id","creationTimestamp":"$TIMESTAMP","eventBodyType":"org.kate.common.conversion.TestEventBody","eventBody":{"field1":"test"}}"""
const val JSON_EVENT_WITHOUT_BODY = """{"modelVersion":"1.0","id":"test-id","traceId":"trace-id","creationTimestamp":"$TIMESTAMP","eventBodyType":"org.kate.common.conversion.TestEventBody"}"""
const val JSON_REQUEST = """{"modelVersion":"1.0","id":"test-id","traceId":"trace-id","creationTimestamp":"$TIMESTAMP","parentRequestId":null,"replyTopic":null,"requestBodyType":"org.kate.common.conversion.TestRequestBody","requestBody":{"field1":"test"}}"""
const val JSON_REQUEST_WITHOUT_BODY = """{"modelVersion":"1.0","id":"test-id","traceId":"trace-id","creationTimestamp":"$TIMESTAMP","parentRequestId":null,"replyTopic":null,"requestBodyType":"org.kate.common.conversion.TestRequestBody"}"""
const val JSON_RESPONSE = """{"modelVersion":"1.0","id":"test-id","traceId":"trace-id","creationTimestamp":"$TIMESTAMP","requestId":"1-1","responseBodyType":"org.kate.common.conversion.TestResponseBody","responseBody":{"field1":"test"}}"""
const val JSON_RESPONSE_WITHOUT_BODY = """{"modelVersion":"1.0","id":"test-id","traceId":"trace-id","creationTimestamp":"$TIMESTAMP","requestId":"1-1","responseBodyType":"org.kate.common.conversion.TestResponseBody"}"""
