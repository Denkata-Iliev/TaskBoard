package org.example.taskboard.auth

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.example.taskboard.auth.jwt.JwtService
import org.example.taskboard.exceptions.BadCredentialsException
import org.example.taskboard.exceptions.ConflictException
import org.example.taskboard.fixtures.aUser
import org.example.taskboard.fixtures.anAuthRequest
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AuthServiceTests {
    private val userRepository = mockk<UserRepository>()
    private val passwordEncoder = mockk<PasswordEncoder>()
    private val jwtService = mockk<JwtService>()
    private val authService = AuthServiceImpl(userRepository, passwordEncoder, jwtService)

    @Test
    fun `when email exists register throws conflict exception`() = runTest {
        coEvery { userRepository.existsByEmail(any()) } returns true

        assertFailsWith<ConflictException> {
            authService.register(anAuthRequest())
        }
    }

    @Test
    fun `when email does not exist register saves user`() = runTest {
        coEvery { userRepository.existsByEmail(any()) } returns false
        every { passwordEncoder.encode(any()) } returns "hashed_password"

        val id = UUID.randomUUID()
        coEvery { userRepository.save(any<BoardUser>()) } answers { firstArg<BoardUser>().copy(id = id) }

        val req = anAuthRequest()
        val result = authService.register(req)

        assertEquals(id, result.id)
        assertEquals(req.email, result.email)

        coVerify(exactly = 1) {
            userRepository.save(match<BoardUser> {
                it.email == req.email && it.passwordHash == "hashed_password" && it.passwordHash != req.password
            })
        }
    }

    @Test
    fun `when email does not exist, login throws bad credentials exception`() = runTest {
        coEvery { userRepository.findByEmail(any()) } returns null

        assertFailsWith<BadCredentialsException> { authService.login(anAuthRequest()) }
    }

    @Test
    fun `when password does not match, login throws bad credentials exception`() = runTest {
        coEvery { userRepository.findByEmail(any()) } returns aUser()

        every { passwordEncoder.matches(any(), any()) } returns false

        assertFailsWith<BadCredentialsException> { authService.login(anAuthRequest()) }
    }

    @Test
    fun `when email and password are correct, login returns jwt`() = runTest {
        coEvery { userRepository.findByEmail(any()) } returns aUser()

        every { passwordEncoder.matches(any(), any()) } returns true
        every { jwtService.generateToken(any()) } returns "jwt"

        val response = authService.login(anAuthRequest())

        assertEquals("jwt", response.jwt)
        verify(exactly = 1) { jwtService.generateToken(any()) }
    }
}