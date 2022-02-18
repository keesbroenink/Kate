package org.kate.examples.cars.common.monitor.inbound

import org.kate.domain.KateErrorMessageBody
import org.kate.domain.KateEvent
import org.kate.domain.KateEventReceivedCallback
import org.kate.examples.cars.common.domain.CarValueResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CarMonitorEventHandler: KateEventReceivedCallback<CarValueResponse> {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(CarMonitorEventHandler::class.java)
    }

    override fun invoke(event: KateEvent) {
        LOGGER.info("Monitoring events; this one just came in $event")
    }

}

@Component
class CarMonitorErrorHandler: KateEventReceivedCallback<KateErrorMessageBody> {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(CarMonitorErrorHandler::class.java)
    }

    override fun invoke(event: KateEvent) {
        LOGGER.info("Monitoring errors; this one just came in $event")
    }
}
