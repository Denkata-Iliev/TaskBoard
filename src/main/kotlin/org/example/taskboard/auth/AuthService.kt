package org.example.taskboard.auth

interface AuthService {
    suspend fun register(request: RegisterRequest): UserResponse
    suspend fun login(request: LoginRequest): JwtResponse
}