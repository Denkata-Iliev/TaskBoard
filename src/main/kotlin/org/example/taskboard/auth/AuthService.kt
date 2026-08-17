package org.example.taskboard.auth

interface AuthService {
    suspend fun register(request: AuthRequest): UserResponse
    suspend fun login(request: AuthRequest): String
}