package ru.snowadv.comaprbackend.security.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import java.net.InetAddress

@Component
@ConfigurationProperties("app.jwtsec")
class SecurityProperties {

    var jwtSecret: String? = null
    var jwtExpirationMs: Long = 0L

}