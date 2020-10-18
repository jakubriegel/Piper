package eu.jrie.put.piper.piperhomeservice.domain.house

import org.springframework.stereotype.Service

@Service
class HousesService (
        private val repository: HousesRepository
) {
    fun houseById(id: String) = repository.findById(id)
}
