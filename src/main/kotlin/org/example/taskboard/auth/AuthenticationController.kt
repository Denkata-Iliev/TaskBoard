package org.example.taskboard.auth

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthenticationController(private val authService: AuthService) {
    @PostMapping("/register")
    suspend fun register(@RequestBody @Valid request: AuthRequest): ResponseEntity<UserResponse> {
        val user = authService.register(request)
        return ResponseEntity.ok(user)
    }
}