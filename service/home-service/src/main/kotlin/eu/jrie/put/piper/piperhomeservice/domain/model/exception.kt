package eu.jrie.put.piper.piperhomeservice.domain.model

import eu.jrie.put.piper.piperhomeservice.infra.exception.PiperNotFoundException

class ModelNotFoundException(modelId: String) : PiperNotFoundException("model", modelId)
