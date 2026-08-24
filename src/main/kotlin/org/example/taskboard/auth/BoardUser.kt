package org.example.taskboard.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

// Intentionally named BoardUser, so conflicts with Spring's User classes won't occur
@Table("users")
data class BoardUser(
    @Id val id: UUID? = null,
    val email: String,
    @Column("password_hash")
    val passwordHash: String,
    @Column("created_at")
    val createdAt: Instant = Instant.now(),
)

data class UserResponse(
    val id: UUID?,
    val email: String,
    val createdAt: Instant
)

fun BoardUser.toResponse() = UserResponse(
    id = this.id,
    email = this.email,
    createdAt = this.createdAt
)

data class RegisterRequest(
    @NotBlank(message = AuthErrorMessages.EMAIL_MUST_NOT_BE_EMPTY)
    @Email(message = AuthErrorMessages.EMAIL_MUST_BE_VALID)
    val email: String,

    @Pattern(
        regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()\\-_+=]).{8,20}$",
        message = AuthErrorMessages.PASSWORD_REQUIREMENTS
    )
    val password: String
)

data class LoginRequest(
    @NotBlank(message = AuthErrorMessages.EMAIL_MUST_NOT_BE_EMPTY)
    val email: String,

    @NotBlank(message = AuthErrorMessages.PASSWORD_MUST_NOT_BE_EMPTY)
    val password: String
)

data class JwtResponse(val jwt: String)