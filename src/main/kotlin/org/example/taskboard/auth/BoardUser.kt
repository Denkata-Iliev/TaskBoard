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

data class AuthRequest(
    @NotBlank(message = "not blank email")
    @Email(message = "email validation error")
    val email: String,

    @NotBlank(message = "not blank password")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()\\-_+=]).{8,20}$", message = "invalid password")
    val password: String
)