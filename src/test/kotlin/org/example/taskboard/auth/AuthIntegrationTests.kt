package org.example.taskboard.auth

import org.example.taskboard.fixtures.anAuthRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureWebTestClient
class AuthIntegrationTests(@Autowired private val client: WebTestClient) {

    companion object {
        @Container
        @ServiceConnection
        private val postgresContainer = PostgreSQLContainer("postgres:latest")
    }

    @Test
    fun `with valid email and password on register, user is saved in db`() {
        val req = anAuthRequest()
        client.post()
            .uri("/auth/register")
            .bodyValue(req)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$.id").isNotEmpty
            .jsonPath("$.email").isEqualTo(req.email)
    }

    @Test
    fun `already existing email on register returns conflict error`() {
        val req = anAuthRequest(email = "already@exists.com")
        client.post()
            .uri("/auth/register")
            .bodyValue(req)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$.id").isNotEmpty
            .jsonPath("$.email").isEqualTo(req.email)

        client.post()
            .uri("/auth/register")
            .bodyValue(req)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody()
            .jsonPath("$.errors[0].field").isEqualTo(null)
            .jsonPath("$.errors[0].fieldErrors[0]")
                .isEqualTo(String.format(AuthErrorMessages.EMAIL_ALREADY_EXISTS, req.email))
    }

    @Test
    fun `valid email and password on login return jwt`() {
        val req = anAuthRequest(email = "login@test.test", password = "Password123#")
        client.post()
            .uri("/auth/register")
            .bodyValue(req)
            .exchange()
            .expectStatus().isCreated

        client.post()
            .uri("/auth/login")
            .bodyValue(req)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.jwt").isNotEmpty
    }

    @Test
    fun `invalid email and password on login returns bad-credentials error`() {
        val req = anAuthRequest(email = "invalid@email.com", password = "invalid")
        client.post()
            .uri("/auth/login")
            .bodyValue(req)
            .exchange()
            .expectStatus().isUnauthorized
            .expectBody()
            .jsonPath("$.errors[0].field").isEqualTo(null)
            .jsonPath("$.errors[0].fieldErrors[0]").isEqualTo(AuthErrorMessages.INVALID_EMAIL_OR_PASSWORD)
    }

    @Test
    fun `empty email on register returns not-empty error`() {
        assertValidationError(
            request = anAuthRequest(email = ""),
            field = "email",
            errorMessage = AuthErrorMessages.EMAIL_MUST_NOT_BE_EMPTY
        )
    }

    @Test
    fun `malformed email on register returns format error`() {
        assertValidationError(
            request = anAuthRequest(email = "invalid"),
            field = "email",
            errorMessage = AuthErrorMessages.EMAIL_MUST_BE_VALID
        )
    }

    @Test
    fun `empty or invalid password on register returns password error`() {
        assertValidationError(
            request = anAuthRequest(password = ""),
            field = "password",
            errorMessage = AuthErrorMessages.PASSWORD_REQUIREMENTS
        )

        assertValidationError(
            request = anAuthRequest(password = "invalid"),
            field = "password",
            errorMessage = AuthErrorMessages.PASSWORD_REQUIREMENTS
        )
    }

    @Test
    fun `invalid email and password on register both return respective errors`() {
        val req = anAuthRequest(email = "invalid", password = "invalid")
        client.post()
            .uri("/auth/register")
            .bodyValue(req)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.errors[0].field").isEqualTo("email")
            .jsonPath("$.errors[0].fieldErrors[0]").isEqualTo(AuthErrorMessages.EMAIL_MUST_BE_VALID)
            .jsonPath("$.errors[1].field").isEqualTo("password")
            .jsonPath("$.errors[1].fieldErrors[0]").isEqualTo(AuthErrorMessages.PASSWORD_REQUIREMENTS)
    }

    private fun assertValidationError(request: AuthRequest, field: String, errorMessage: String) {
        client.post()
            .uri("/auth/register")
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.errors[0].field").isEqualTo(field)
            .jsonPath("$.errors[0].fieldErrors[0]").isEqualTo(errorMessage)
    }
}