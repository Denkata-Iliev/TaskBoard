package org.example.taskboard.auth

import org.example.taskboard.exceptions.ConflictException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl(
    private val repository: UserRepository,
    private val encoder: PasswordEncoder
) : AuthService {
    override suspend fun register(request: AuthRequest): UserResponse {
        if (repository.existsByEmail(request.email)) {
            throw ConflictException("User with email ${request.email} already exists.")
        }

        val user = BoardUser(
            email = request.email,
            passwordHash = encoder.encode(request.password)!!
        )

        val dbUser = repository.save(user)

        return dbUser.toResponse()
    }

    override suspend fun login(request: AuthRequest): String {
        TODO("Not yet implemented")
    }
}