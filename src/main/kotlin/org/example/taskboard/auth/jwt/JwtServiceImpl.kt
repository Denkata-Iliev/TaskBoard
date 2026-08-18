package org.example.taskboard.auth.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.example.taskboard.auth.BoardUser
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtServiceImpl(
    @Value($$"${spring.application.name}") private val appName: String,
    @Value($$"${app.jwt.secret}") private val signingKey: String,
    @Value($$"${app.jwt.expiration}") private val expirationMs: Long,
) : JwtService {
    private val key = Keys.hmacShaKeyFor(signingKey.toByteArray())

    override fun generateToken(user: BoardUser): String {
        val now = Date()
        val expirationDate = Date(now.time + expirationMs)
        return Jwts
            .builder()
            .issuer(appName)
            .issuedAt(now)
            .expiration(expirationDate)
            .subject(user.id!!.toString())
            .signWith(key)
            .compact()
    }

    override fun validateTokenAndGetUserId(token: String): UUID? {
        return runCatching {
            val parsedClaims = Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)

            UUID.fromString(parsedClaims.payload.subject)
        }.getOrNull()
    }
}