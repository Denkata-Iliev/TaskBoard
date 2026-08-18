package org.example.taskboard.auth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.example.taskboard.auth.jwt.JwtServiceImpl
import org.example.taskboard.fixtures.aUser
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import kotlin.test.assertEquals

class JwtTests {
    private val signingKey = "a".repeat(32)
    private val expirationMs = 60 * 60 * 1000L //1hr
    private val service = JwtServiceImpl(
        appName = "appName",
        signingKey = signingKey,
        expirationMs = expirationMs,
    )

    @Test
    fun `with a valid user, a valid jwt is generated`() {
        val user = aUser()
        val token = service.generateToken(user)

        val claims = Jwts
            .parser()
            .verifyWith(Keys.hmacShaKeyFor(signingKey.toByteArray()))
            .build()
            .parseSignedClaims(token)
            .payload

        assertEquals(user.id.toString(), claims.subject)
    }

    @Test
    fun `with a valid signed token, userId is extracted`() {
        val user = aUser()
        val now = Date()
        val exp = Date(now.time + expirationMs)
        val token = Jwts
            .builder()
            .signWith(Keys.hmacShaKeyFor(signingKey.toByteArray()))
            .subject(user.id!!.toString())
            .issuedAt(now)
            .expiration(exp)
            .compact()

        val userId = service.validateTokenAndGetUserId(token)

        assertEquals(user.id, userId)
    }

    @Test
    fun `with a valid user, a valid token is generated and the correct userId is extracted`() {
        val user = aUser()
        val token = service.generateToken(user)
        val userId = service.validateTokenAndGetUserId(token)

        assertEquals(user.id, userId)
    }

    @Test
    fun `with an expired token, extracted userId is null`() {
        val user = aUser()
        val token = Jwts
            .builder()
            .signWith(Keys.hmacShaKeyFor(signingKey.toByteArray()))
            .subject(user.id!!.toString())
            .issuedAt(Date.from(Instant.now().minus(2, ChronoUnit.HOURS)))
            .expiration(Date.from(Instant.now().minus(1, ChronoUnit.HOURS)))
            .compact()

        val userId = service.validateTokenAndGetUserId(token)
        assertNull(userId)
    }

    @Test
    fun `with the wrong signing key, extracted userId is null`() {
        val user = aUser()
        val now = Date()
        val exp = Date(now.time + expirationMs)
        val token = Jwts
            .builder()
            .signWith(Keys.hmacShaKeyFor("b".repeat(32).toByteArray()))
            .subject(user.id!!.toString())
            .issuedAt(now)
            .expiration(exp)
            .compact()

        val userId = service.validateTokenAndGetUserId(token)
        assertNull(userId)
    }
}