package eu.jrie.put.piper.piperhomeservice.domain.user

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import reactor.core.publisher.Mono

@Document
data class User (
        @Id
        val id: String,
        val login: String,
        val secret: String,
        val houses: Set<String>,
        val roles: Set<String>
) : UserDetails {
    override fun getPassword() = secret
    override fun getUsername() = login
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true
    override fun getAuthorities() = roles.map { "ROLE_$it"}
            .map { SimpleGrantedAuthority(it) }
            .toSet()
}

interface UserRepository : ReactiveMongoRepository<User, String> {
    fun findByLogin(login: String): Mono<User>
}

fun Authentication.asUser() = this.principal as User
