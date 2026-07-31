package org.example.taskboard.user

interface UserService {
    suspend fun register(request: AuthRequest): UserResponse
    suspend fun login(request: AuthRequest): String
}