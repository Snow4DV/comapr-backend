package ru.snowadv.comaprbackend.security.jwt

import java.security.Key;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import ru.snowadv.comaprbackend.security.service.UserDetailsImpl
import javax.crypto.SecretKey

@Component
class JwtUtils {
    @field:Value("\${app.jwtsec.jwtSecret}")
    private val jwtSecret: String? = null

    @field:Value("\${app.jwtsec.jwtExpirationMs}")
    private val jwtExpirationMs = 86400000

    val key: SecretKey by lazy { Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret)) }


    fun generateJwtToken(authentication: Authentication): String {
        val userPrincipal: UserDetailsImpl = authentication.principal as UserDetailsImpl

        return Jwts.builder()
            .subject((userPrincipal.username))
            .issuedAt(Date())
            .expiration(Date(Date().time + jwtExpirationMs))
            .signWith(key)
            .compact()
    }

    fun getUserNameFromJwtToken(token: String?): String {
        return Jwts.parser().verifyWith(key).build()
            .parseSignedClaims(token).payload.subject
    }

    fun validateJwtToken(authToken: String?): Boolean {
        try {
            Jwts.parser().verifyWith(key).build().parse(authToken)
            return true
        } catch (e: MalformedJwtException) {
            logger.error("Invalid JWT token: {}", e.message)
        } catch (e: ExpiredJwtException) {
            logger.error("JWT token is expired: {}", e.message)
        } catch (e: UnsupportedJwtException) {
            logger.error("JWT token is unsupported: {}", e.message)
        } catch (e: IllegalArgumentException) {
            logger.error("JWT claims string is empty: {}", e.message)
        }
        return false
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(JwtUtils::class.java)
    }
}
