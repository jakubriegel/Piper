package eu.jrie.put.piper.piperhomeservice.config

import eu.jrie.put.piper.piperhomeservice.config.SecurityConfig.UserRole.ADMIN
import eu.jrie.put.piper.piperhomeservice.config.SecurityConfig.UserRole.HOUSE
import eu.jrie.put.piper.piperhomeservice.config.SecurityConfig.UserRole.OWNER
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.server.SecurityWebFilterChain


@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    fun users(): MapReactiveUserDetailsService {
        val house1: UserDetails = User.withDefaultPasswordEncoder()
                .username("house-1")
                .password("pass")
                .roles(HOUSE)
                .build()
        val owner1: UserDetails = User.withDefaultPasswordEncoder()
                .username("owner-1")
                .password("pass")
                .roles(OWNER)
                .build()
        val house2: UserDetails = User.withDefaultPasswordEncoder()
                .username("house-2")
                .password("pass")
                .roles(HOUSE)
                .build()
        val owner2: UserDetails = User.withDefaultPasswordEncoder()
                .username("owner-2")
                .password("pass")
                .roles(OWNER)
                .build()
        val admin: UserDetails = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("pass")
                .roles(ADMIN)
                .build()
        return MapReactiveUserDetailsService(house1, owner1, house2, owner2, admin)
    }

    @Bean
    fun configure(http: ServerHttpSecurity): SecurityWebFilterChain {
        http
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers("/house/**").hasAnyRole(HOUSE)
                .anyExchange().hasAnyRole(ADMIN)
                .and()
                .httpBasic().and()
                .formLogin().disable()
        return http.build()
    }

    object UserRole {
        const val HOUSE = "HOUSE"
        const val OWNER = "OWNER"
        const val ADMIN = "ADMIN"
    }

}
