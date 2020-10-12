package eu.jrie.put.piper.piperhomeservice.domain.user

import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import reactor.core.publisher.Mono
import java.util.*

@Table("piper_user")
data class User (
        @PrimaryKey
        val login: String,
        val secret: String,
        val houses: Set<UUID>,
        val roles: Set<String>
) : UserDetails {
    override fun getPassword() = secret
    override fun getUsername() = login
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true
    override fun getAuthorities(): Set<SimpleGrantedAuthority> = roles.map { "ROLE_$it"}
            .map { SimpleGrantedAuthority(it) }
            .toSet()
}

interface UserRepository : ReactiveCassandraRepository<User, UUID> {
    fun findByLogin(login: String): Mono<User>
}

fun Authentication.asUser() = this.principal as User
