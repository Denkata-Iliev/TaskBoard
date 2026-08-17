package org.example.taskboard.fixtures

import org.example.taskboard.auth.AuthRequest
import org.example.taskboard.auth.BoardUser
import java.time.Instant
import java.util.UUID

fun aUser(
    id: UUID = UUID.randomUUID(),
    email: String = "mail@mail.com",
    password: String = "hashed_password",
    createDate: Instant = Instant.now()
) = BoardUser(id, email, password, createDate)

fun anAuthRequest(
    email: String = "mail@mail.com",
    password: String = "Password123#"
) = AuthRequest(email, password)