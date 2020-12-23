package eu.jrie.put.piper.piperhomeservice.api.infra

import eu.jrie.put.piper.piperhomeservice.domain.house.HousesService
import eu.jrie.put.piper.piperhomeservice.domain.user.User
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.asFlux

@Component
class DevicesProvider (
        private val housesService: HousesService
) {
    fun getDevices(user: User): Mono<Map<String, String>> {
        return housesService.getDevices(user)
                .map { it.id to it.roomId }
                .asFlux()
                .collectList()
                .map { it.toTypedArray() }
                .map { mapOf(*it) }
    }
}
