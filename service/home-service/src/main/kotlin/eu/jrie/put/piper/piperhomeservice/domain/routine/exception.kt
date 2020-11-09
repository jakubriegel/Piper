package eu.jrie.put.piper.piperhomeservice.domain.routine

import eu.jrie.put.piper.piperhomeservice.domain.house.Device
import eu.jrie.put.piper.piperhomeservice.domain.house.DeviceEvent
import eu.jrie.put.piper.piperhomeservice.infra.exception.PiperException

class NoModelException : PiperException("Predictions not available.")
