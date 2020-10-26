package eu.jrie.put.piper.piperhomeservice.domain.routine

import eu.jrie.put.piper.piperhomeservice.domain.house.Device
import eu.jrie.put.piper.piperhomeservice.domain.house.DeviceEvent
import eu.jrie.put.piper.piperhomeservice.infra.exception.PiperException

class NoModelException : PiperException("Predictions not available.")

class NotDeviceEventException(
        device: Device, event: DeviceEvent
) : PiperException("Provided event is not triggered by given device.") {
    override val details = mapOf("device" to device, "event" to event)
}
