package eu.jrie.put.piper.piperhomeservice.api.message.util

import org.springframework.security.core.Authentication

internal object Auth : Authentication {
    override fun getName() = throw IllegalStateException()
    override fun getAuthorities() = throw IllegalStateException()
    override fun getCredentials() = throw IllegalStateException()
    override fun getDetails() = throw IllegalStateException()
    override fun getPrincipal() = throw IllegalStateException()
    override fun isAuthenticated() = throw IllegalStateException()
    override fun setAuthenticated(isAuthenticated: Boolean) = throw IllegalStateException()
}
