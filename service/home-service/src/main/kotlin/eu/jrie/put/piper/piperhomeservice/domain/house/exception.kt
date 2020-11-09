package eu.jrie.put.piper.piperhomeservice.domain.house

import eu.jrie.put.piper.piperhomeservice.infra.exception.PiperException
import eu.jrie.put.piper.piperhomeservice.infra.exception.PiperNotFoundException

class DeviceNotFoundException(deviceId: String) : PiperNotFoundException("device", deviceId)

class EventNotFoundException(eventId: String) : PiperNotFoundException("event", eventId)

class NotDeviceEventException(
        device: Device, event: DeviceEvent
) : PiperException("Provided event is not triggered by given device.") {
    override val details = mapOf("device" to device, "event" to event)
}
