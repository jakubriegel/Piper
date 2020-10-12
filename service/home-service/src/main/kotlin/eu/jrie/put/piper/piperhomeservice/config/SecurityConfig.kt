package eu.jrie.put.piper.piperhomeservice.config

import eu.jrie.put.piper.piperhomeservice.config.SecurityConfig.UserRole.ADMIN
import eu.jrie.put.piper.piperhomeservice.config.SecurityConfig.UserRole.HOUSE
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain


@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

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
