package org.example.taskboard.auth

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface UserRepository : CoroutineCrudRepository<BoardUser, UUID> {
    suspend fun existsByEmail(email: String): Boolean
    suspend fun findByEmail(email: String): BoardUser?
}