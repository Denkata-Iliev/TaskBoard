package org.example.taskboard.auth

import org.example.taskboard.auth.jwt.JwtService
import org.example.taskboard.exceptions.ConflictException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl(
    private val repository: UserRepository,
    private val encoder: PasswordEncoder,
    private val jwtService: JwtService
) : AuthService {
    override suspend fun register(request: AuthRequest): UserResponse {
        if (repository.existsByEmail(request.email)) {
            throw ConflictException(String.format(AuthErrorMessages.EMAIL_ALREADY_EXISTS, request.email))
        }

        val user = BoardUser(
            email = request.email,
            passwordHash = encoder.encode(request.password)!!
        )

        val dbUser = repository.save(user)

        return dbUser.toResponse()
    }

    override suspend fun login(request: AuthRequest): JwtResponse {
        val user = repository.findByEmail(request.email) ?: throw BadCredentialsException(AuthErrorMessages.INVALID_EMAIL_OR_PASSWORD)

        if (!encoder.matches(request.password, user.passwordHash)) {
            throw BadCredentialsException(AuthErrorMessages.INVALID_EMAIL_OR_PASSWORD)
        }

        val jwt = jwtService.generateToken(user)
        return JwtResponse(jwt)
    }
}