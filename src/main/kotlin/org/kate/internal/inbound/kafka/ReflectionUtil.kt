package org.kate.internal.inbound.kafka

import java.lang.reflect.Method

fun getBodyClassname(methods: Array<Method>) : String {
    methods.forEach {
        if (it.name == "getBodyType") {
            it.parameters.forEach {
                if (it.name == "bodyType") {
                    val x = it.parameterizedType
                    return x.typeName
                }
            }
        }
    }
    return ""
}
