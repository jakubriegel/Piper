package eu.jrie.put.piper.piperhomeservice.domain.user

import eu.jrie.put.piper.piperhomeservice.infra.common.nextUUID
import eu.jrie.put.piper.piperhomeservice.infra.security.SecurityConfig.UserRole.HOUSE
import eu.jrie.put.piper.piperhomeservice.infra.security.SecurityConfig.UserRole.USER
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserService (
        private val userRepository: UserRepository
) {
    fun addUser(houseId: String) = add(houseId, setOf(USER))

    fun addHouse(houseId: String) = add(houseId, setOf(HOUSE))

    private fun add(houseId: String, roles: Set<String>) = Mono.just(nextUUID)
            .map { id -> User(id, id, "{noop}secret", houseId, roles) }
            .flatMap { userRepository.save(it) }
}