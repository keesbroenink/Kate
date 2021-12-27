package org.kate.examples.cars.common.monitor

import org.kate.common.KateErrorMessageBody
import org.kate.common.KateEvent
import org.kate.common.KateEventReceivedCallback
import org.kate.examples.cars.common.domain.CarValueResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CarMonitorService: KateEventReceivedCallback<CarValueResponse> {
    companion object {
        val LOGGER = LoggerFactory.getLogger(CarMonitorService::class.java)
    }

    override fun invoke(event: KateEvent) {
        LOGGER.info("Monitoring events; this one just came in $event")
    }

}

@Component
class CarMonitorErrorsService: KateEventReceivedCallback<KateErrorMessageBody> {
    companion object {
        val LOGGER = LoggerFactory.getLogger(CarMonitorErrorsService::class.java)
    }

    override fun invoke(event: KateEvent) {
        LOGGER.info("Monitoring errors; this one just came in $event")
    }
}
