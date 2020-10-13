package eu.jrie.put.piper.piperhomeservice.config

import eu.jrie.put.piper.piperhomeservice.config.SecurityConfig.UserRole.ADMIN
import eu.jrie.put.piper.piperhomeservice.config.SecurityConfig.UserRole.HOUSE
import eu.jrie.put.piper.piperhomeservice.config.SecurityConfig.UserRole.USER
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.DELETE
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpMethod.PUT
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    fun configure(http: ServerHttpSecurity): SecurityWebFilterChain = http
            .csrf().disable()
            .authorizeExchange()
            .pathMatchers("/house/**").hasAnyRole(HOUSE, ADMIN)
            .pathMatchers("/routines/**").hasAnyRole(USER, ADMIN)
            .anyExchange().hasAnyRole(ADMIN).and()
            .cors().and()
            .httpBasic().and()
            .formLogin().disable()
            .build()

    @Bean
    fun corsConfiguration(): CorsConfigurationSource {
        val cors = CorsConfiguration().apply {
            addAllowedMethod(GET)
            addAllowedMethod(POST)
            addAllowedMethod(PUT)
            addAllowedMethod(DELETE)
            addAllowedOrigin("*")
            addAllowedHeader("*")
        }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/routines/**", cors)
        }
    }

    object UserRole {
        const val HOUSE = "HOUSE"
        const val USER = "USER"
        const val ADMIN = "ADMIN"
    }

}
