package eu.jrie.put.piper.piperhomeservice.infra.server

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory
import org.springframework.boot.web.embedded.netty.SslServerCustomizer
import org.springframework.boot.web.server.Http2
import org.springframework.boot.web.server.Ssl
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.stereotype.Component

@Component
class NettyCustomizer : WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {
    override fun customize(serverFactory: NettyReactiveWebServerFactory) {
        val ssl = Ssl().apply {
            isEnabled = true
            keyStore = "classpath:piper.p12"
            keyAlias = "piper"
            keyPassword = "secret"
            keyStorePassword = "secret"
            keyStoreType = "PKCS12"
        }
        val http2 = Http2().apply {
            isEnabled = false
        }
        serverFactory.addServerCustomizers(SslServerCustomizer(ssl, http2, null))
    }
}
