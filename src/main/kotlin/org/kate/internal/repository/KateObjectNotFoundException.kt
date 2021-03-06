package org.kate.internal.repository

class KateObjectNotFoundException(objectId: String, objectType: ObjectType) : RuntimeException("$objectType id $objectId not found" ) {
}

enum class ObjectType(val s: String) {REQUEST("Request with"),RESPONSE_FOR_REQUEST("Response with request"),EVENT("Event with")}
