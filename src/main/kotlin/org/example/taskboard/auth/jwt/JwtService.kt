package org.example.taskboard.auth.jwt

import org.example.taskboard.auth.BoardUser
import java.util.UUID

interface JwtService {
    fun generateToken(user: BoardUser): String
    fun validateTokenAndGetUserId(token: String): UUID?
}