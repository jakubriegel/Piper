package eu.jrie.put.piper.piperhomeservice.infra.security

import eu.jrie.put.piper.piperhomeservice.infra.security.SecurityConfig.UserRole.ADMIN
import eu.jrie.put.piper.piperhomeservice.infra.security.SecurityConfig.UserRole.HOUSE
import eu.jrie.put.piper.piperhomeservice.infra.security.SecurityConfig.UserRole.MODEL_BUILDER
import eu.jrie.put.piper.piperhomeservice.infra.security.SecurityConfig.UserRole.USER
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
            //events endpoints
            .pathMatchers("/events").hasAnyRole(HOUSE, ADMIN)
            // routines endpoints
            .pathMatchers(GET, "/routines").hasAnyRole(USER, HOUSE, ADMIN)
            .pathMatchers("/routines/**").hasAnyRole(USER, ADMIN)
            // suggestions endpoints
            .pathMatchers(GET, "/suggestions/continuation").hasAnyRole(USER, ADMIN)
            .pathMatchers(GET, "/suggestions/routines").hasAnyRole(USER, ADMIN)
            // houses endpoints
            .pathMatchers(POST, "/houses").hasRole(ADMIN)
            .pathMatchers(GET, "/houses").hasAnyRole(USER, ADMIN)
            .pathMatchers("/houses/rooms/**").hasAnyRole(USER, ADMIN)
            .pathMatchers("/houses/devices/**").hasAnyRole(USER, ADMIN)
            .pathMatchers("/houses/devices/types/**").hasAnyRole(USER, ADMIN)
            .pathMatchers("/houses/schema").hasAnyRole(HOUSE, ADMIN)
            // model endpoints
            .pathMatchers("/models/**").hasRole(MODEL_BUILDER)
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
            registerCorsConfiguration("/suggestions/**", cors)
            registerCorsConfiguration("/houses/**", cors)
        }
    }

    object UserRole {
        const val HOUSE = "HOUSE"
        const val USER = "USER"
        const val ADMIN = "ADMIN"
        const val MODEL_BUILDER = "MODEL_BUILDER"
    }

}
