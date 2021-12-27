package org.kate.examples.cars

import org.kate.common.KateInitialization
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(KateInitialization::class)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
