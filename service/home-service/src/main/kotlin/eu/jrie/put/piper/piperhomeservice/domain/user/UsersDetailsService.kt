package eu.jrie.put.piper.piperhomeservice.domain.user

import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono.defer
import reactor.core.publisher.Mono.error


@Service
class UserDetailsService (
        private val userRepository: UserRepository
) : ReactiveUserDetailsService {

    override fun findByUsername(username: String) = userRepository.findByLogin(username)
            .switchIfEmpty(defer { error(UsernameNotFoundException("User $username Not Found")) })
            .map { it as UserDetails }
}
