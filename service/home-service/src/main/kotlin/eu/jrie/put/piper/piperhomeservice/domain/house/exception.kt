package eu.jrie.put.piper.piperhomeservice.domain.house

import eu.jrie.put.piper.piperhomeservice.infra.exception.PiperNotFoundException

class DeviceNotFoundException(deviceId: String) : PiperNotFoundException("device", deviceId)

class EventNotFoundException(eventId: String) : PiperNotFoundException("event", eventId)
